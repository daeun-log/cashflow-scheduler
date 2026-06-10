# 자금 흐름 스케줄러 Analysis
22421674 이다은 rosedaeuni@gmail.com

### revision history
| date | version | description | author |
|:---|:---|:---|:---|
| 2026.05.07 | v1.0 | 초안 | 이다은 |
| 2026.06.03 | v1.1 | 데이터 저장 방식(로컬 파일) 및 UI 구조(표) 변경으로 인한 설계 전면 수정 | 이다은 |
| 2026.06.07 | v2.0 | 안드로이드 앱(MVVM 아키텍처 및 Room DB) 구조로 설계 전면 개편 | 이다은 |
| 2026.06.10 | v3.0 | 월별 타임라인 네비게이션, 잔액 조정 내역, 필터 기반 검색, 상세보기 팝업 등 기능 전면 개정 | 이다은 |

# 1. Introduction

### 1.1. Summary

자동이체, OTT 등 구독 시스템이 늘어나며 날짜 기반의 고정지출이 증가함에 따라 대학생 및 사회초년생들은 제한된 수입 내에서 다음 주나 다음 달 발생할 수 있는 일시적인 잔액 부족 상황을 미리 파악하고 대처하기 어렵다. 기존의 가계부 앱들은 과거의 소비를 기록하는 데에 그치기 때문에 미래의 자금 흐름을 파악하고 이에 대비하는 데에 한계가 있다.
따라서 이를 해결하기 위해 수입과 지출 예정일을 기반으로 월별 자금 흐름을 시각화하는 예측형 금융 스케줄링 시스템인 "자금 흐름 스케줄러"를 만들게 되었다.

### 1.2. Business Goals

"자금 흐름 스케줄러"의 주요 목적은 정기적으로 발생하는 고정지출과 불규칙적인 일회성 지출을 하나의 월별 타임라인으로 통합하여 미래의 예상 잔액을 도출하는 것이다. 특히 오늘 이후 30일 이내에 통장 잔고가 0원 이하로 떨어지는 예정일을 사전에 경고함으로써 사용자의 재무적 위험을 방지하고 계획적인 소비를 돕는다.

### 1.3. Technical Goals

- 안드로이드 모바일 환경으로 개발하며, MVVM 아키텍처와 안드로이드 내장 데이터베이스인 Room DB를 활용하여 안정적인 데이터 영속성과 UI 상태 관리를 보장한다.

- 유저는 회원가입/로그인이 가능하고, 로그인 시 아이디 존재 여부와 비밀번호 일치 여부를 단계별로 검증하여 사용자에게 명확한 안내 메시지를 제공한다.

- 기능을 실행할 때 결과를 빠르게 보여줄 수 있도록 Room DB의 범위 쿼리와 Java TimSort 기반의 날짜순 정렬 알고리즘을 활용한다.


# 2. Use case analysis

### 2.1. Use case diagram
<img width="470" height="1331" alt="Use Case Diagram" src="https://github.com/user-attachments/assets/0dd8a87d-1d3b-4a71-a095-19aded205f3c" />

### 2.2. Use case description

### 2.2.1. 계정 및 권한 관리

#### 2.2.1.1. Use Case #1 : Join

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary| 새로운 사용자가 시스템에 계정을 생성하고 Room DB에 등록하여 접근 권한을 마련하는 기능|
|Scope|자금 흐름 스케줄러|
|Level|User Level|
|Author|Lee daeun|
|Last Updated|2026-06-10|
|Status|Analysis|
|Primary Actor|User|
|Preconditions|시스템이 실행되어야 한다|
|Trigger|로그인 화면에서 "회원가입" 버튼을 클릭할 때|
|Success Post Condition|새로운 계정 정보가 Room DB의 User 테이블에 저장되고, 로그인 화면으로 전환됨|
|Failed Post Condition|계정이 생성되지 않고 회원가입 화면에 머묾|

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1|사용자가 회원가입 화면에서 ID와 PW를 입력하고 가입을 요청한다|
|2|시스템은 입력된 ID를 기반으로 Room DB를 조회하여 이미 존재하는 계정인지 중복 확인을 거친다|
|3|유효성 검사가 완료되면 신규 User Entity를 Room DB에 Insert하고, 회원가입 성공 메시지를 출력한 뒤 로그인 화면으로 전환한다|

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2|2a. 입력된 ID가 이미 존재하는 경우 <br>2a.1 중복된 ID라는 오류 메시지를 띄우고 회원가입 화면을 유지한다|

|RELATED INFORMATION||
|:---|:---|
|Performance|<=3초|
|Frequency|사용자 당 1회|
|Concurrency||
|Due Date||
|Etc||

#### 2.2.1.2. Use Case #2 : Login

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary|기존 사용자가 Room DB의 단계별 계정 인증을 통해 시스템에 접속하여 접근 권한을 획득하는 기능|
|Scope|자금 흐름 스케줄러|
|Level|User Level|
|Author|Lee daeun|
|Last Updated|2026-06-10|
|Status|Analysis|
|Primary Actor|User|
|Preconditions|해당 사용자의 계정 정보가 Room DB의 User 테이블에 존재해야 한다|
|Trigger|로그인 화면에서 ID와 PW를 입력하고 로그인 버튼을 클릭할 때|
|Success Post Condition|로그인에 성공하여 해당 유저의 금융 데이터가 로드되고 메인 대시보드(현재 월 타임라인)로 진입|
|Failed Post Condition|로그인에 실패하여 시스템 접근이 거부되고 로그인 화면에 머묾|

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1|사용자가 ID와 PW를 입력하고 로그인을 시도한다|
|2|시스템은 Room DB에서 입력된 ID의 존재 여부를 먼저 확인하고, 존재할 경우 비밀번호 일치 여부를 검증한다|
|3|인증에 성공하면 "로그인 되었습니다" 토스트를 출력하고, FinanceViewModel을 통해 해당 유저의 금융 데이터를 로드하여 메인 대시보드로 전환한다|

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2|2a. 입력된 ID가 존재하지 않는 경우 <br>2a.1 "존재하지 않는 아이디입니다" 메시지를 출력하고 로그인 화면을 유지한다 <br>2b. ID는 존재하나 PW가 일치하지 않는 경우 <br>2b.1 "비밀번호가 올바르지 않습니다" 메시지를 출력하고 로그인 화면을 유지한다|

|RELATED INFORMATION||
|:---|:---|
|Performance|<=3초|
|Frequency|사용자 당 시스템 접속 시마다|
|Concurrency||
|Due Date||
|Etc||

#### 2.2.1.3. Use Case #3 : Logout

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary|사용자의 시스템 접근 권한(세션)을 확인 팝업을 거쳐 안전하게 해제하고 로그인 화면으로 돌아가는 기능|
|Scope|자금 흐름 스케줄러|
|Level|User Level|
|Author|Lee daeun|
|Last Updated|2026-06-10|
|Status|Analysis|
|Primary Actor|User|
|Preconditions|시스템에 정상적으로 로그인된 상태여야 한다|
|Trigger|하단 네비게이션의 로그아웃 버튼을 클릭할 때|
|Success Post Condition|확인 팝업에서 승인 후 세션이 초기화되고 로그인 화면으로 전환됨|
|Failed Post Condition|사용자가 팝업에서 취소를 선택하여 메인 화면을 유지함|

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1|사용자가 로그아웃 버튼을 클릭한다|
|2|시스템은 "정말 로그아웃 하시겠습니까?" 확인 팝업을 띄운다|
|3|사용자가 승인하면 ViewModel의 유저 세션 LiveData를 초기화하고 로그인 화면으로 전환한다|

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2|2a. 사용자가 팝업에서 취소를 선택한 경우 <br>2a.1 로그아웃을 중단하고 메인 화면으로 복귀한다|

|RELATED INFORMATION||
|:---|:---|
|Performance|<=3초|
|Frequency|사용자 당 시스템 종료 시 1회|
|Concurrency||
|Due Date||
|Etc||

### 2.2.2. 금융 내역 관리

#### 2.2.2.1. Use Case #4 : Add Event

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary|수입, 일회성 지출, 고정 지출을 단일 내역 추가 화면에서 통합 등록하는 기능|
|Scope|자금 흐름 스케줄러|
|Level|User Level|
|Author|Lee daeun|
|Last Updated|2026-06-10|
|Status|Analysis|
|Primary Actor|User|
|Preconditions|시스템에 로그인되어 있어야 한다|
|Trigger|대시보드의 "내역 추가" 버튼을 클릭할 때|
|Success Post Condition|입력된 내역이 Room DB에 저장되고 타임라인이 자동 갱신됨|
|Failed Post Condition|내역이 등록되지 않음|

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1|사용자가 내역 추가 화면을 열고 날짜(달력 선택 또는 직접 입력), 구분(수입/지출/고정지출), 카테고리, 상세내용, 금액을 입력한다|
|2|저장 버튼을 누르면 시스템은 날짜와 금액의 유효성을 검사한다|
|3|유효성 검사 통과 시 Room DB에 즉시 저장하고 대시보드로 복귀하여 타임라인을 자동 갱신한다|

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2|2a. 날짜 또는 금액이 누락되거나 형식에 맞지 않는 경우 <br>2a.1 해당 항목을 안내하는 토스트 메시지를 띄우고 입력 화면을 유지한다|

|RELATED INFORMATION||
|:---|:---|
|Performance|<=3초|
|Frequency|새로운 금융 이벤트 발생 시|
|Concurrency||
|Due Date||
|Etc||

#### 2.2.2.2. Use Case #5 : Modify Event

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary|기존에 등록된 내역의 날짜, 금액, 상세내용을 상세보기 팝업을 통해 변경하는 기능|
|Scope|자금 흐름 스케줄러|
|Level|User Level|
|Author|Lee daeun|
|Last Updated|2026-06-10|
|Status|Analysis|
|Primary Actor|User|
|Preconditions|시스템에 1개 이상의 내역이 등록되어 있어야 한다|
|Trigger|타임라인에서 내역의 상세보기(⋯) 버튼을 클릭한 뒤 "내역 수정" 버튼을 클릭할 때|
|Success Post Condition|변경된 정보로 내역이 갱신되고 타임라인의 예상 잔액이 재계산됨|
|Failed Post Condition|정보가 변경되지 않고 기존 내역이 유지됨|

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1|사용자가 타임라인에서 수정할 내역의 상세보기를 열고 수정 버튼을 클릭한다|
|2|수정 다이얼로그에서 날짜, 금액, 상세내용을 변경하고 저장을 요청한다|
|3|시스템은 유효성 검사 후 Room DB의 해당 레코드를 Update하고 타임라인을 재계산한다|

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2|2a. 수정된 날짜 또는 금액이 유효하지 않은 경우 <br>2a.1 오류 토스트를 출력하고 수정 전의 원본 데이터를 유지한다|

|RELATED INFORMATION||
|:---|:---|
|Performance|<=3초|
|Frequency|내역 정보를 잘못 입력했을 때|
|Concurrency||
|Due Date||
|Etc||

#### 2.2.2.3. Use Case #6 : Delete Event

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary|기존에 등록된 불필요한 내역을 상세보기 팝업을 통해 삭제하는 기능|
|Scope|자금 흐름 스케줄러|
|Level|User Level|
|Author|Lee daeun|
|Last Updated|2026-06-10|
|Status|Analysis|
|Primary Actor|User|
|Preconditions|시스템에 1개 이상의 내역이 등록되어 있어야 한다|
|Trigger|타임라인에서 내역의 상세보기(⋯) 버튼을 클릭한 뒤 "내역 삭제" 버튼을 클릭할 때|
|Success Post Condition|해당 내역이 Room DB에서 삭제되고 타임라인이 갱신됨|
|Failed Post Condition|해당 내역이 삭제되지 않고 유지됨|

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1|사용자가 타임라인에서 삭제할 내역의 상세보기를 열고 삭제 버튼을 클릭한다|
|2|시스템은 재확인 다이얼로그를 띄워 삭제 여부를 묻는다|
|3|사용자가 승인하면 해당 내역을 Room DB에서 Delete하고 타임라인을 갱신한다|

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2|2a. 사용자가 재확인 다이얼로그에서 취소를 선택한 경우 <br>2a.1 삭제 과정을 중단하고 이전 화면으로 복귀한다|

|RELATED INFORMATION||
|:---|:---|
|Performance|<=3초|
|Frequency|내역이 취소되었거나 잘못 입력했을 때|
|Concurrency||
|Due Date||
|Etc||

### 2.2.3. 타임라인 조회

#### 2.2.3.1. Use Case #7 : View Monthly Timeline

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary|월 네비게이터를 통해 원하는 달을 선택하고, 해당 월의 수입/지출 내역과 일별 누적 예상 잔액을 타임라인 형태로 조회하는 기능|
|Scope|자금 흐름 스케줄러|
|Level|User Level|
|Author|Lee daeun|
|Last Updated|2026-06-10|
|Status|Analysis|
|Primary Actor|User|
|Preconditions|시스템에 로그인되어 있어야 한다|
|Trigger|메인 대시보드 접근 시, 또는 월 네비게이터의 좌우 화살표를 클릭할 때|
|Success Post Condition|선택한 월의 수입/지출 내역이 날짜순으로 정렬되고 일별 예상 잔액이 정상 출력됨|
|Failed Post Condition|연산 또는 정렬 오류로 타임라인이 표출되지 않음|

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1|사용자가 월 네비게이터에서 좌우 화살표를 클릭하여 조회할 달을 선택한다|
|2|시스템은 해당 월의 시작 잔액(직전 월 말 잔액)을 계산하고, 해당 월의 수입/지출 내역을 Room DB에서 조회하여 날짜순으로 정렬한다|
|3|정렬된 내역을 기준으로 잔액 증감을 순차 연산하여 항목별 예상 잔액과 함께 타임라인을 화면에 출력한다|

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2|2a. 해당 월에 등록된 내역이 없는 경우 <br>2a.1 빈 타임라인 화면을 표시한다|

|RELATED INFORMATION||
|:---|:---|
|Performance|<=5초|
|Frequency|메인 화면 접근 시 및 월 변경 시|
|Concurrency||
|Due Date||
|Etc||

#### 2.2.3.2. Use Case #8 : View Event Detail

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary|타임라인의 특정 내역을 선택하여 상세 정보 및 고정 지출의 향후 예정 일정을 팝업으로 확인하는 기능|
|Scope|자금 흐름 스케줄러|
|Level|User Level|
|Author|Lee daeun|
|Last Updated|2026-06-10|
|Status|Analysis|
|Primary Actor|User|
|Preconditions|타임라인에 1개 이상의 내역이 존재해야 한다|
|Trigger|타임라인에서 특정 내역의 상세보기(⋯) 버튼을 클릭할 때|
|Success Post Condition|상세 팝업이 열려 날짜, 구분, 카테고리, 상세내용, 금액이 표시되고, 고정지출의 경우 향후 4개월 예정 내역도 함께 표시됨|
|Failed Post Condition|팝업이 열리지 않음|

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1|사용자가 타임라인 항목의 상세보기(⋯) 버튼을 클릭한다|
|2|시스템은 해당 내역의 날짜, 구분, 카테고리, 상세내용, 금액을 팝업에 표시한다|
|3|해당 내역이 고정 지출인 경우 향후 4개월의 예정 발생 날짜와 금액을 팝업 하단에 추가로 표시한다|

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2|2a. 사용자가 팝업의 X 버튼을 클릭하거나 팝업 외부를 터치한 경우 <br>2a.1 팝업을 닫고 타임라인 화면으로 복귀한다|

|RELATED INFORMATION||
|:---|:---|
|Performance|<=1초|
|Frequency|내역의 상세 정보나 고정 지출 일정을 확인하고자 할 때|
|Concurrency||
|Due Date||
|Etc||

### 2.2.4. 데이터 탐색 및 조회

#### 2.2.4.1. Use Case #9 : Search Financial Event

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary|날짜 범위, 카테고리, 상세내역, 내역유형 필터를 조합하여 수입 및 지출 내역을 검색하는 기능|
|Scope|자금 흐름 스케줄러|
|Level|User Level|
|Author|Lee daeun|
|Last Updated|2026-06-10|
|Status|Analysis|
|Primary Actor|User|
|Preconditions|검색을 수행할 금융 데이터가 존재해야 한다|
|Trigger|검색 탭에서 필터를 선택하고 검색 버튼을 누를 때|
|Success Post Condition|선택한 필터 조건에 부합하는 수입/지출 내역 리스트가 화면에 표시됨|
|Failed Post Condition|검색 수행 실패 시 기존 화면을 유지함|

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1|사용자가 날짜 범위(시작일~종료일), 카테고리, 상세내역 키워드, 내역유형(수입/지출/고정지출) 중 원하는 필터를 선택하여 조건을 설정한다|
|2|검색 버튼을 누르면 시스템은 현재 로드된 타임라인 데이터에서 설정된 조건과 일치하는 내역만 필터링한다|
|3|필터링된 검색 결과 리스트를 화면에 출력한다|

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2|2a. 조건에 일치하는 내역이 없는 경우 <br>2a.1 "검색 결과가 없습니다"라는 안내 메시지를 출력한다|

|RELATED INFORMATION||
|:---|:---|
|Performance|<=3초|
|Frequency|특정 기간이나 카테고리의 내역을 찾고자 할 때|
|Concurrency||
|Due Date||
|Etc||

### 2.2.5. 분석 및 시뮬레이션

#### 2.2.5.1. Use Case #10 : Check Overdraft Alert

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary|타임라인 연산 결과, 오늘 이후 30일 이내에 예상 잔액이 0원 미만으로 떨어지는 날짜가 감지될 경우 화면 상단에 경고 배너를 표시하는 기능|
|Scope|자금 흐름 스케줄러|
|Level|System Level|
|Author|Lee daeun|
|Last Updated|2026-06-10|
|Status|Analysis|
|Primary Actor|System|
|Preconditions|현재 잔액 또는 미래 수입/지출 내역이 1개 이상 존재해야 한다|
|Trigger|월별 타임라인 계산이 완료될 때 자동으로 발생|
|Success Post Condition|"n월 m일 잔액 고갈 예상" 경고 배너가 화면 상단에 빨간색으로 표시됨|
|Failed Post Condition|조건을 충족했음에도 경고 배너가 발생하지 않음|

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1|시스템이 현재 월 타임라인 계산을 완료한다|
|2|오늘 이후 날짜의 항목 중 예상 잔액이 최초로 0원 미만이 되는 시점을 탐색한다|
|3|해당 날짜가 오늘로부터 30일 이내인 경우 "n월 m일 잔액 고갈 예상" 경고 배너를 화면 상단에 표시한다|

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2|2a. 오늘 이후 30일 이내에 잔액이 0원 미만으로 떨어지지 않는 경우 <br>2a.1 경고 배너를 표시하지 않는다|

|RELATED INFORMATION||
|:---|:---|
|Performance|<=10초|
|Frequency|타임라인 계산 완료 시 자동 발생|
|Concurrency||
|Due Date||
|Etc||

### 2.2.6. 잔액 관리

#### 2.2.6.1. Use Case #11 : Adjust Balance

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary|내역 미입력 등으로 실제 잔액과 계산 잔액 간에 차이가 발생했을 때, 목표 잔액과 변경 원인을 입력하면 차액만큼의 잔액 조정 내역을 타임라인에 자동 추가하는 기능|
|Scope|자금 흐름 스케줄러|
|Level|User Level|
|Author|Lee daeun|
|Last Updated|2026-06-10|
|Status|Analysis|
|Primary Actor|User|
|Preconditions|시스템에 로그인되어 있어야 한다|
|Trigger|현재 잔액 카드를 클릭할 때|
|Success Post Condition|차액이 잔액조정 카테고리의 수입 또는 지출 내역으로 오늘 날짜에 타임라인에 추가되고, 현재 잔액이 입력한 목표 잔액과 일치하게 됨|
|Failed Post Condition|잔액 조정이 수행되지 않음|

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1|사용자가 현재 잔액 카드를 클릭하여 잔액 수정 다이얼로그를 연다|
|2|실제 보유 중인 목표 잔액과 변경 원인(기존 목록 선택 또는 신규 추가)을 입력하고 저장을 요청한다|
|3|시스템은 현재 계산 잔액과의 차액을 산출하여, 차액이 양수이면 잔액조정 수입 내역으로, 음수이면 잔액조정 지출 내역으로 오늘 날짜에 Room DB에 저장한다|

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2|2a. 입력한 목표 잔액이 현재 계산 잔액과 동일한 경우 <br>2a.1 아무런 내역도 추가하지 않고 다이얼로그를 닫는다|

|RELATED INFORMATION||
|:---|:---|
|Performance|<=3초|
|Frequency|실제 잔액과 계산 잔액이 달라졌을 때|
|Concurrency||
|Due Date||
|Etc||

### 2.2.7. 시스템 관리

#### 2.2.7.1. Use Case #12 : Save Data

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary|사용자가 데이터를 추가/수정/삭제/조정할 때마다 백그라운드 스레드(ExecutorService)를 통해 Room DB에 즉시 반영하여 데이터 유실을 방지하는 기능|
|Scope|자금 흐름 스케줄러|
|Level|System Level|
|Author|Lee daeun|
|Last Updated|2026-06-10|
|Status|Analysis|
|Primary Actor|System|
|Preconditions|데이터의 추가, 수정, 삭제, 잔액 조정 트랜잭션이 발생해야 한다|
|Trigger|수입/지출 내역의 추가, 수정, 삭제, 잔액 조정 이벤트가 발생할 때|
|Success Post Condition|변경 사항이 Room DB에 성공적으로 반영되어 영구 보존되고, LiveData를 통해 UI 타임라인이 자동 갱신됨|
|Failed Post Condition|저장에 실패하여 데이터 변경 사항이 반영되지 않음|

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1|사용자가 UI에서 금융 데이터를 추가, 수정, 삭제하거나 잔액 조정을 수행한다|
|2|ViewModel을 통해 Repository에 데이터 변경 요청이 전달되며, 백그라운드 스레드(ExecutorService)에서 Room DB에 즉시 저장된다|
|3|DB 변경 사항은 LiveData를 통해 UI 타임라인에 자동으로 반영된다|

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2||

|RELATED INFORMATION||
|:---|:---|
|Performance|<=3초|
|Frequency|데이터 변동 시마다|
|Concurrency||
|Due Date||
|Etc||


# 3. Domain analysis

### 3.1. View (Activity / Fragment)
사용자와 직접 상호작용하는 안드로이드의 Activity 및 Fragment 클래스들이다. ViewModel을 구독(observe)하여 LiveData의 변경 사항을 UI에 반영한다.
ex) 화면 이동, 토스트 메시지, 다이얼로그, 팝업 표시 등

### 3.2. User
시스템에 가입하여 스케줄러를 사용하게 될 유저들의 계정 정보를 가지는 Room Entity 클래스이다.
ex) userId(PK), userPw 등

### 3.3. Income
수입 내역의 속성(날짜, 금액, 카테고리, 상세내용)을 정의하는 Room Entity 클래스이다. 잔액 조정 시 생성되는 잔액조정 수입 내역도 이 테이블에 저장된다.
ex) 수입 금액, 수입 날짜, 카테고리, 상세내용(source) 등

### 3.4. Expense
지출 내역의 속성(날짜, 금액, 카테고리, 상세내용, 고정 여부, 반복 주기)을 정의하는 Room Entity 클래스이다. 잔액 조정 시 생성되는 잔액조정 지출 내역도 이 테이블에 저장된다.
ex) 지출 금액, 지출 날짜, 카테고리, 상세내용, isFixed, cycle 등

### 3.5. AccountViewModel & AccountRepository
유저의 회원가입, 로그인, 로그아웃 등 계정 접근 권한 로직을 처리하는 ViewModel과, Room DB의 User 테이블에 접근하여 유저 데이터를 검증하는 Repository 역할을 하는 컴포넌트이다.
ex) ID 중복 확인, 단계별 로그인 인증(아이디 존재→비밀번호 일치), 세션 초기화 등

### 3.6. FinanceViewModel & FinanceRepository
유저의 수입/지출 내역을 UI에 연결해주는 ViewModel과, 백그라운드에서 Room DB에 접근해 데이터를 추가, 수정, 삭제(CRUD)하는 Repository 역할을 하는 컴포넌트이다. 잔액 조정(adjustBalance) 및 월별 타임라인 계산(calculateMonth) 로직도 포함한다.
ex) 내역 추가/수정/삭제, 월별 타임라인 계산, 잔액 조정 내역 생성 등

### 3.7. Timeline
월별 타임라인 연산을 담당하는 클래스이다. 해당 월의 시작 잔액을 기준으로 수입/지출 내역을 날짜순으로 정렬하고 항목별 누적 예상 잔액을 계산한다. 고정 지출은 해당 월 내 발생하는 것만 반복 전개한다.
ex) 날짜순 정렬(TimSort), 누적 예상 잔액 연산, 마이너스 잔액 발생 여부 판별 등

### 3.8. Room Database & DAO (Data Layer)
안드로이드 공식 로컬 DB 라이브러리인 Room을 활용하여 사용자의 금융 데이터(Entity)를 SQLite 테이블에 안전하게 영구 저장하고 쿼리(DAO)하는 기능을 담당한다. DB 스키마 변경 시 Migration을 통해 기존 데이터를 안전하게 유지한다.
ex) UserDao, FinanceDao의 CRUD 쿼리, 범위 조회(getIncomesByRange), 고정지출 조회(getFixedExpenses) 등

### 3.9. CategoryManager & BalanceManager
앱의 카테고리 목록(수입/지출/잔액조정 원인)과 잔액 조정 원인 목록을 SharedPreferences에 저장하고 관리하는 유틸리티 클래스이다. 사용자가 새 카테고리 또는 원인을 추가하면 즉시 저장되어 다음 접속 시에도 유지된다.
ex) 카테고리 목록 조회/추가, 잔액 조정 원인 목록 조회/추가 등

# 4. User Interface prototype

### 4.1. 로그인 및 회원가입 화면
<img width="1080" height="2400" alt="login join" src="https://github.com/user-attachments/assets/c54cf7f7-162a-4eb1-9b94-212e9541398c" />

- 로그인 화면: 아이디/비밀번호 입력, 로그인 버튼, 회원가입 이동 링크
- 회원가입 화면: 아이디/비밀번호 입력, 가입하기 버튼, 로그인 이동 링크
- 로그인 단계별 안내: 존재하지 않는 아이디 / 비밀번호 오류 / 로그인 성공 토스트

### 4.2. 메인 대시보드 화면
<img width="1080" height="2400" alt="maindashboard" src="https://github.com/user-attachments/assets/0863b6a3-bc48-4094-9c67-bc6652c4f627" />

- 현재 잔액 카드 (클릭 시 잔액 수정 다이얼로그)
- 30일 내 고갈 경고 배너 (조건 충족 시만 표시)
- 내역 추가 버튼
- 월 네비게이터 (< 2026년 6월 >)
- 타임라인 헤더 (날짜 / 구분 / 금액 / 예상잔액)
- 타임라인 목록 ([구분]카테고리 형식, 상세보기 ⋯ 버튼, 마이너스 행 빨간색 강조)

### 4.3. 내역 추가 화면
<img width="1080" height="2400" alt="addstatement" src="https://github.com/user-attachments/assets/ade0e567-00d2-4daf-89ba-cc80b1f8d81b" />

- 뒤로가기 버튼 + "거래 추가" 제목
- 날짜: 달력 버튼 또는 직접 입력(YYYY-MM-DD)
- 구분: 수입 / 지출(일회성) / 고정지출 스피너
- 카테고리: 기존 목록 스피너 + "+ 추가" 버튼(이름 입력 후 "추가" 버튼 클릭)
- 상세내용 입력
- 금액 입력
- 반복 주기 입력 (고정지출 선택 시만 표시)
- 추가 버튼

### 4.4. 내역 상세보기 팝업
<img width="1080" height="2400" alt="detail" src="https://github.com/user-attachments/assets/025d7961-7769-4286-b982-50ee0abfcad0" />

- 우측 상단 X 버튼
- 날짜 / 구분 / 카테고리 / 상세내용 / 금액 표시
- 고정지출인 경우 향후 4개월 예정 일정 표시
- 내역 수정 / 내역 삭제 버튼

### 4.5. 검색 화면
<img width="1080" height="2400" alt="search" src="https://github.com/user-attachments/assets/7c89a954-9401-4df0-a850-407a4d57a52c" />

- 필터 칩: 날짜 / 카테고리 / 상세내역 / 내역유형
- 각 칩 클릭 시 맞춤 팝업 (날짜: 달력, 카테고리: 직접입력+목록선택, 상세내역: 키워드 입력, 내역유형: 단일 선택)
- 적용된 필터 요약 표시 및 초기화 버튼
- 검색 결과 목록

### 4.6. 잔액 수정 다이얼로그
<img width="1080" height="2400" alt="balanceedit" src="https://github.com/user-attachments/assets/d7b46abf-8443-40d1-9e2c-c0077051cb09" />

- 현재 잔액 표시 및 목표 잔액 입력
- 변경 원인 스피너 (기존 목록 선택 또는 신규 추가)
- 취소 / 저장 버튼

# 5. Glossary

| Term (용어) | Description (설명) |
| :--- | :--- |
| 타임라인 (Timeline) | 선택한 달의 수입 및 지출 내역들이 날짜순으로 정렬되어, 각 항목별 예상 잔액과 함께 리스트 형태로 제공되는 화면 |
| 일회성 지출 (Expense) | 정기적이지 않고 일회성으로 발생하는 소비 |
| 고정 지출 (Fixed Expense) | 구독료, 교통비, 통신비 등 매월 정기적인 주기로 반복 발생하는 예상 지출. 등록된 결제일 기준으로 매월 자동 전개됨 |
| 오버드래프트 (Overdraft) | 타임라인 연산 결과 특정 시점의 예상 잔액이 0원 미만으로 떨어져 마이너스 잔고가 되는 상태. 해당 행은 붉은색으로 강조 표시됨 |
| 잔액 조정 (Balance Adjustment) | 내역 미입력 등으로 실제 잔액과 계산 잔액 사이에 차이가 발생했을 때, 차액을 수입 또는 지출 내역으로 타임라인에 자동 추가하여 현재 잔액을 보정하는 기능 |
| 월 네비게이터 (Monthly Navigator) | 대시보드 화면에서 좌우 화살표로 조회 달을 전환하는 UI 컴포넌트 (< 2026년 6월 >) |
| Room Database | 외부 서버 없이 안드로이드 기기 내부에 사용자의 금융 데이터를 테이블 형태로 저장하는 공식 로컬 데이터베이스 라이브러리 |
| MVVM 아키텍처 | Model-View-ViewModel의 약자로, 안드로이드 앱의 UI와 데이터 처리 로직을 분리하여 DB 변경 사항이 LiveData를 통해 UI에 실시간 반영되도록 설계하는 모바일 개발 표준 아키텍처 패턴 |

# 6. References

* 토스 (Toss) - 소비/지출, 정기 결제 관리 탭: 매 달 나가는 구독료나 자동이체 예정일과 금액을 캘린더 형태로 미리 보여주고, 결제일이 다가오면 알림을 보내주는 아이디어 참고.
* 뱅크샐러드 (BankSalad) - 가계부 자산 타임라인 기능: 일회성 지출인 일반 지출과 매 달 발생하는 고정 지출을 날짜별로 통합하여 하나의 흐름으로 보여주고, 카테고리별로 분류하는 방식 참고.
* 왓섭 (Whatssub) - 정기구독 관리 플랫폼: 매 달 빠져나가는 수많은 고정 지출(OTT, 멤버십 등) 일정을 통합 관리하고, 결제일 전에 갱신 여부나 잔액 상태를 묻는 사전 경고 시스템 참고.
