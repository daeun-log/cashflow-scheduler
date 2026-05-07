# 자금 흐름 스케줄러 Analysis
22421674 이다은 rosedaeuni@gmail.com

### revision history
| date | version | description | author |
|:---|:---|:---|:---|
| 2026.05.07 | v1.0 | 초안 | 이다은 |

# 1. Introduction

### 1.1. Summary

자동이체, OTT 등 구독 시스템이 늘어나며 날짜 기반의 고정지출이 증가함에 따라 대학생 및 사회초년생들은 제한된 수입 내에서 다음 주나 다음 달 발생할 수 있는 일시적인 잔액 부족 상황을 미리 파악하고 대처하기 어렵다. 기존의 가계부 앱들은 과거의 소비를 기록하는데에 그치기 때문에 미래의 자금 흐름을 파악하고 이에 대비하는 데에 한계가 있다.
따라서 이를 해결하기위해 수입과 지출 예정일을 기반으로 미래의 자금 흐름을 시각화 하는 예측형 금융 스케줄링 시스템인 "자금 흐름 스케줄러"를 만들게 되었다.

### 1.2. Business Goals

"자금 흐름 스케줄러"의 주요 목적은 정기적으로 발생하는 고정지출과 불규칙 적인 반발성 지출을 하나의 타임라인으로 통합하여 미래의 예상 잔액을 도출하는 것이다. 특히 통장 잔고가 0원 이하로 떨어지는 예정일을 사전에 경고함으로써 사용자의 재무적 위험을 방지하고 계획적인 소비를 돕는다.

### 1.3. Technical Goals

- 서버에 회원 정보, 자금흐름 등 관련 정보를 저장하게 하고, 이 정보들은 개인의 것이기에 본인만 수정할 수 있게 한다

- 유저는 회원가입/로그인이 가능하고, 로그인을 해야 해당 정보에 접근할 수 있게 한다.

- 기능을 실행할 때 결과를 빠르게 보여줄 수 있도록 적합한 알고리즘을 선택한다.


# 2. Use case analysis

### 2.1. Use case diagram


### 2.2. Use case description

### 2.2.1. 계정 및 권한 관리

#### 2.2.1.1. Use Case #1 : Join

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary| 새로운 사용자가 시스템에 계정을 만들고, 접근 권한을 마련하는 기능|
|Scope|자금 흐름 스케줄러|
|Level|User Level|
|Author|Lee daeun|
|Last Updated|2026-05-07|
|Status|Analysis|
|Primary Actor|User|
|Preconditions|시스템이 실행되어야한다|
|Trigger|로그인 화면에서 "회원가입"버튼을 클릭할 때|
|Success Post Condition|새로운 계정이 시스템에 저장되고, 로그인 화면으로 전환됨|
|Failed Post Condition|께정이 생성되지 않고 회원가입 화면에 머묾|

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1|사용자가 회원가입 화면에서 사용자 정보를 입력하고 가입요청을 한다|
|2|사스탬은 압력된 ID의 중복 여부를 확인한다|
|3|모두 완료되면 계정을 저장하고, 회원가입 성공 메세지를 출력한다|

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2|2a. 입력된 ID가 이미 존재하는 경우 <br>2a.1 중복된 ID라는 오류 메세지를 띄우고 초기 회원가입 화면으로 돌아감|

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
|Summary|기존 사용자가 시스템에 접속하여 접근 권한 획득하는 기능|
|Scope|자금 흐름 스케줄러|
|Level|User Level|
|Author|Lee daeun|
|Last Updated|2026-05-07|
|Status|Analysis|
|Primary Actor|User|
|Preconditions|회원가입된 계정이 존재해야 한다|
|Trigger|로그인 화면에서 ID와 PW를 입력하고, 로그인 버튼을 클릭할 때|
|Success Post Condition|로그인에 성공해서 자신의 스케줄러 화면에 진입|
|Failed Post Condition|로그인에 실패하여 시스텝 접근 거부되고 로그인화면에 머묾|

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1|사용자가 자신의 ID와 PW를 입력하고 로그인을 시도한다|
|2|시스템은 저장된 정보와 비교하여 일치 여부를 판단한다|
|3|정보가 일치하면 접근 권한을 부여하고 사용자의 스케줄러 화면을 띄운다|

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2|2a ID/PW가 일치하지 않은 경우 <br>2a.1 로그인 실패 메시지를 출력하고 초기 로그인 화면으로 돌아감|

|RELATED INFORMATION||
|:---|:---|
|Performance|<=3초|
|Frequency|사용자 당 시스텝 접속 시마다|
|Concurrency||
|Due Date||
|Etc||

#### 2.2.1.3. Use Case #3 : Logout

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary|사용자가 시스템 접속을 종료하고 접근 권한을 해제하는 기능|
|Scope|자금 흐름 스케줄러|
|Level|User Level|
|Author|Lee daeun|
|Last Updated|2026-05-07|
|Status|Analysis|
|Primary Actor|User|
|Preconditions|시스템에 정상적으로 로그인된 상태여야 한다|
|Trigger|로그아웃 버튼을 클릭할 때|
|Success Post Condition|접속 상태가 해제되고 로그인 화면으로 돌아감|
|Failed Post Condition|로그아웃에 실패함|

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1|사용자가 로그아웃을 요청한다|
|2|사스템은 사용자의 겁근권한을 회수한다|
|3|로그인 화면으로 전환하여 접속을 종료한다|

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2||

|RELATED INFORMATION||
|:---|:---|
|Performance|<=3초|
|Frequency|사용자 당 시스템 종료시 1회|
|Concurrency||
|Due Date||
|Etc||

### 2.2.2. 수입 및 지출 관리

#### 2.2.2.1. Use Case #4 : Add Income

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary|새로운 수입 내역(날짜, 금액, 카테고리)를 시스템에 등록하는 기능|
|Scope|자금 흐름 스케줄러|
|Level|User Level|
|Author|Lee daeun|
|Last Updated|2026-05-07|
|Status|Analysis|
|Primary Actor|User|
|Preconditions|시스템에 로그인되어 있어야 한다|
|Trigger|수입추가버튼을 클릭할 때|
|Success Post Condition|입력된 수입이 타임라인 데이터베이스에 정상반영됨|
|Failed Post Condition|수입 내역이 등록되지 않음|

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1|사용자가 월급, 용돈 등 수입 정보를 입력하기 위해 수입 추가 화면을 연다|
|2|날짜, 금액, 카테고리 등 필요한 정보를 입력하고 저장 버튼을 누른다|
|3|시스템은 유효성 검사 후 정보를 시스템 내역에 추가하고 타임라인을 갱신한다|

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2|2a 필수 정보(금액 또는 날짜)가 누락되거나 형식에 맞지 않는 경우 <br>2a.1 올바른 형식을 요구하는 경고창을 띄우고 기존 입력 화면을 유지한다|

|RELATED INFORMATION||
|:---|:---|
|Performance|<=3초|
|Frequency|새로운 수입 발생 시|
|Concurrency||
|Due Date||
|Etc||

#### 2.2.2.2. Use Case #5 : Modify Income

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary|기존에 등록된 수입 내역의 정보(날짜, 금액, 카테고리)를 변경하는 기능|
|Scope|자금 흐름 스케줄러|
|Level|User Level|
|Author|Lee daeun|
|Last Updated|2026-05-07|
|Status|Analysis|
|Primary Actor|User|
|Preconditions|시스템에 1개 이상의 수입 내역이 등록되어 있어야 한다|
|Trigger|기존 수입 내역을 선택하고 수정 버튼을 클릭할 때|
|Success Post Condition|변경된 정보로 내역이 덮어쓰기 되며 잔액 계산이 수정됨|
|Failed Post Condition|정보가 변경되지 않고 기존 내역 유지|

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1|사용자가 타임라인에서 수정할 수입 내역을 선택한다|
|2|금액이나 날짜 등 변동된 정보를 입력하고 수정을 요청한다|
|3|시스템은 기존 내역을 덮어쓰기 형태로 변경하고 전체 자금 흐름을 재계산한다|

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2|2a 수정 입력된 정보가 유효하지 않은 경우 <br>2a.1 오류 메시지를 출력하고, 수정 전의 원본 데이터를 유지한다|

|RELATED INFORMATION||
|:---|:---|
|Performance|<=3초|
|Frequency|수입 정보를 잘못 입력했을 때|
|Concurrency||
|Due Date||
|Etc||

#### 2.2.2.3. Use Case #6 : Delete Income

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary|기존에 등록된 불필요한 수입 내역을 시스템에서 삭제하는 기능|
|Scope|자금 흐름 스케줄러|
|Level|User Level|
|Author|Lee daeun|
|Last Updated|2026-05-07|
|Status|Analysis|
|Primary Actor|User|
|Preconditions|시스템에 한개 이상의 수입 내역이 등록되어 있어야 한다|
|Trigger|기존 수입 내역을 선택하고 삭제 버튼을 누를 때|
|Success Post Condition|해당 수입 내역이 시스템에서 완전히 삭제됨|
|Failed Post Condition|해당 수입 내역이 삭제되지 않고 남아있음|

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1|사용자가 취소되거나 잘못 등록된 수입 내역을 선택하고 삭제를 요청한다|
|2|시스템은 재확인 알림 창을 띄워 삭제 여부를 묻는다|
|3|사용자가 승인하면 해당 내역을 완전히 삭제하고, 타임라인을 갱신한다|

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2|2a 사용자가 재확인 알림 창에서 취소를 선택한 경우 <br>2a.1 삭제 과정을 중단하고 이전 화면으로 복귀한다|

|RELATED INFORMATION||
|:---|:---|
|Performance|<=3초|
|Frequency|수입이 취소되었거나 잘못 입력했을 때|
|Concurrency||
|Due Date||
|Etc||

#### 2.2.2.4. Use Case #7 : Add Expense

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary|새로운 단발성 지출 내역(날짜, 금액, 카테고리)을 등록하는 기능|
|Scope|자금 흐름 스케줄러|
|Level|User Level|
|Author|Lee daeun|
|Last Updated|2026-05-07|
|Status|Analysis|
|Primary Actor|User|
|Preconditions|시스템에 로그인되어 있어야 한다|
|Trigger|지출 추가 버튼을 클릭할 때|
|Success Post Condition|단발성 지출이 시스템 타임라인에 반영됨|
|Failed Post Condition|지출내역이 등록되지 않|

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1|일회성 소비를 기록하기 위해 지출 추가 화면을 연다|
|2|날짜, 금액, 카테고리를 입력하고 저장을 요청한다|
|3|시스템은 내역에 추가하고 예상 잔액을 갱신한다|

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2|2a 필수 정보(금액, 날짜)가 올바르지 않은 경우 <br>2a.1 입력 오류 경고창을 띄우고 데이터 저장을 하지 않는다|

|RELATED INFORMATION||
|:---|:---|
|Performance|<=3초|
|Frequency|단발성 지출 발생 시|
|Concurrency||
|Due Date||
|Etc||

#### 2.2.2.5. Use Case #8 : Modify Expense

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary|단발성 지출 내역(날짜, 금액, 카테고리)을 수정하는 기능|
|Scope|자금 흐름 스케줄러|
|Level|User Level|
|Author|Lee daeun|
|Last Updated|2026-05-07|
|Status|Analysis|
|Primary Actor|User|
|Preconditions|시스템에 한개 이상의 지출 내역이 등록되어 있어야 한|
|Trigger|기존 지출 내역을 선택하고 수정 버튼을 클릭할 때|
|Success Post Condition|변경된 정보로 내역이 갱신되며, 타임라인 잔액이 수정됨|
|Failed Post Condition|지출 정보 갱신에 실패함

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1|사용자가 변경할 지출 내역을 선택한다|
|2|새로운 정보로 변경하여 갱신을 요청한다|
|3|시스템은 기존 데이터를 새로운 정보로 덮어쓰고, 관련 잔액 계산을 수정한다|

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2|2a 수정 입력된 정보가 유효하지 않은 경우 <br>2a.1 오류 메시지를 출력하고, 수정 전의 원본 데이터를 유지한다|

|RELATED INFORMATION||
|:---|:---|
|Performance|<=3초|
|Frequency|단발성 지출의 정보를 잘못 입력했을 때|
|Concurrency||
|Due Date||
|Etc||

#### 2.2.2.6. Use Case #9 : Delete Expense

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary|단발성 지출 내역을 삭제하는 기능|
|Scope|자금 흐름 스케줄러|
|Level|User Level|
|Author|Lee daeun|
|Last Updated|2026-05-07|
|Status|Analysis|
|Primary Actor|User|
|Preconditions|시스템에 한개 이상의 지출 내역이 등록되어 있어야 한다|
|Trigger|기존 지출 내역을 선택하고 삭제 버튼을 클릭할 때|
|Success Post Condition|해당 지출 내역이 시스템에서 삭제됨|
|Failed Post Condition|해당 지출 내역이 삭제되지 않|

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1|사용자가 지출 내역을 선택하고 삭제를 요청한다|
|2|시스템은 재확인 알림 창을 띄워 삭제 여부를 붇는다|
|3|사용자가 승인하면 해당 내역을 제거하고 타임라인을 갱신한다|

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2|2a 사용자가 재확인 알림 창에서 취소를 선택한 경우 <br>2a.1 삭제 과정을 중단하고 이전 화면으로 복귀한다|

|RELATED INFORMATION||
|:---|:---|
|Performance|<=3초|
|Frequency|단발성 지출을 잘못 입력했을 때|
|Concurrency||
|Due Date||
|Etc||

### 2.2.3. 정기 지출 관리

#### 2.2.3.1. Use Case #10 : Add Fixed Expense

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary|매월 정기적으로 발생하는 예상 고정 지출(구독료, 예상 교통비 등)을 시스템에 등록하는 기능|
|Scope|자금 흐름 스케줄러|
|Level|User Level|
|Author|Lee daeun|
|Last Updated|2026-05-07|
|Status|Analysis|
|Primary Actor|User|
|Preconditions|시스템에 로그인되어 있어야 한다|
|Trigger|고정 지출 추가 버튼을 클릭할 때|
|Success Post Condition|입력된 고정 지출이 시스템에 등록되어 타임라인 연산에 자동 포함됨|
|Failed Post Condition|고정 지출 내역이 등록되지 않음|

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1|사용자가 고정 지출 등록 화면을 열어 결제일, 주기, 금액을 입력한다|
|2|입력을 완료하고 저장을 요청한다|
|3|시스템은 유효성 검사 후 타임라인 조회 시 설정된 주기마다 지출이 발생하도록 자동 적용한다|

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2|2a 필수 정보(금액, 결제일, 주기)가 누락되거나 올바르지 않은 경우 <br>2a.1 경고창을 띄우고 데이터 저장을 하지 않은 채 기존 입력 화면을 유지한다|

|RELATED INFORMATION||
|:---|:---|
|Performance|<=3초|
|Frequency|새로운 정기 결제나 구독 서비스가 발생할 |
|Concurrency||
|Due Date||
|Etc||

#### 2.2.3.2. Use Case #11 : Modify Fixed Expense

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary|기존에 등록된 예상 고정 지출의 상세 정보(결제일, 금액, 카테고리 등)를 변경하는 기능|
|Scope|자금 흐름 스케줄러|
|Level|User Level|
|Author|Lee daeun|
|Last Updated|2026-05-07|
|Status|Analysis|
|Primary Actor|User|
|Preconditions|시스템에 개 이상의 고정 지출 내역이 등록되어 있어야 한다|
|Trigger|기존 고정 지출 내역을 선택하고 수정 버튼을 클릭할 때|
|Success Post Condition|변경된 결제일/금액이 갱신되어 이후 타임라인 연산에 일괄 적용됨|
|Failed Post Condition|고정 지출 정보가 갱신되지 않음|

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1|사용자가 변경할 고정 지출 내역을 선택한다|
|2|새로운 정보(결제일, 금액 등)로 변경하여 갱신을 요청한다|
|3|시스템은 기존 데이터를 덮어쓰고, 향후 잔액 계산에 일괄 반영한다|

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2|2a 수정 입력된 정보가 유효하지 않은 경우 <br>2a.1 오류 메시지를 출력하고, 수정 전의 원본 데이터를 유지한다|

|RELATED INFORMATION||
|:---|:---|
|Performance|<=3초|
|Frequency|구독료가 이냉되거나 결제일이 변경되었을 때|
|Concurrency||
|Due Date||
|Etc||

#### 2.2.3.3. Use Case #12 : Delete Fixed Expense

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary|기존에 등록된 예상 고정 지출 내역을 시스템에서 삭제하는 기능|
|Scope|자금 흐름 스케줄러|
|Level|User Level|
|Author|Lee daeun|
|Last Updated|2026-05-07|
|Status|Analysis|
|Primary Actor|User|
|Preconditions|시스템에 개 이상의 고정 지출 내역이 등록되어 있어야 한다|
|Trigger|기존 고정 지출 내역을 선택하고 삭제 버튼을 클릭할 때|
|Success Post Condition|해당 항목이 제거되며, 미래의 잔액 연산 일정에서 완전히 제외됨|
|Failed Post Condition|해당 항목이 삭제되지 않고 유지됨|

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1|사용자가 불필요해진 고정 지출 내역을 선택하고 삭제를 요청한다|
|2|시스템은 재확인 알림 창을 띄워 삭제 여부를 묻는다|
|3|사용자가 승인하면 내역을 시스템에서 삭제하여, 이후의 타임라인 및 예상 잔액 계산에서 제외한다|

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2|2a 사용자가 재확인 알림 창에서 취소를 선택한 경우 <br>2a.1 삭제 과정을 중단하고 이전 화면으로 복귀한다|

|RELATED INFORMATION||
|:---|:---|
|Performance|<=3초|
|Frequency|사용 중이던 정기 구독 서비스를 해지했을 떄|
|Concurrency||
|Due Date||
|Etc||

### 2.2.4. 데아터 탐색 및 조회

#### 2.2.4.1. Use Case #13 : Search Financial Event

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary|특정 키워드(날짜, 금액 범위, 소비 카테고리 등)를 기준으로 수입 및 지출 내역을 검색하는 기능|
|Scope|자금 흐름 스케줄러|
|Level|User Level|
|Author|Lee daeun|
|Last Updated|2026-05-07|
|Status|Analysis|
|Primary Actor|User|
|Preconditions|검색을 수행할 금융 데이터가 존재해야 한다|
|Trigger|검색창에 키워드를 입력하고 검색 버튼을 누를 때|
|Success Post Condition|검색 조건에 부합하는 수입/지출 내역 리스트가 화면에 표시됨|
|Failed Post Condition|검색 수행 실패 시 기존 화면을 유지함|

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1|사용자가 검색 조건(키워드, 날짜, 카테고리 등)을 입력하고 검색을 요청한다|
|2|시스템은 조건과 일치하는 내역만 필터링하여 찾는다|
|3|필터링된 검색 결과 리스트를 화면에 출력한다|

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2|2a 조건에 일치하는 내역이 데이터베이스에 없는 경우 <br>2a.1 "검색 결과가 없습니다"라는 안내 메시지를 출력한다|

|RELATED INFORMATION||
|:---|:---|
|Performance|<=5초|
|Frequency|과거 지출 내역이나 특정 카테고리 소비를 찾고자 할 때|
|Concurrency||
|Due Date||
|Etc||

#### 2.2.4.2. Use Case #14 : View Projected Balance

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary|특정 기간을 설정하여, 날짜순으로 정렬된 자금 흐름 타임라인과 일별 누적 예상 잔액 리포트를 조회하는 기능|
|Scope|자금 흐름 스케줄러|
|Level|User Level|
|Author|Lee daeun|
|Last Updated|2026-05-07|
|Status|Analysis|
|Primary Actor|User|
|Preconditions|기본 잔액 및 개 이상의 수입/지출 데이터가 존재해야 한다|
|Trigger|타임라인 조회 탭을 선택하거나 메인 대시보드에 접근할 때|
|Success Post Condition|수입/지출 내역이 날짜순 정렬되고 일별 예상 누적 잔액이 정상적으로 출력됨|
|Failed Post Condition|연산 또는 정렬 오류로 타임라인이 표출되지 않음|

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1|사용자가 타임라인 조회를 위한 특정 기간을 설정한다|
|2|시스템은 해당 기간의 수입/지출 내역을 날짜순으로 정렬한다|
|3|잔액 증감을 순차적으로 연산하여 일별 누적 예상 잔액 리포트를 화면에 출력한다|

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2|2a 데이터가 너무 많아 정렬 및 연산에 지연이 발생하는 경우 <br>2a.1 화면에 로딩 인디케이터(스피너)를 띄워 처리 중임을 사용자에게 알린다|

|RELATED INFORMATION||
|:---|:---|
|Performance|<=10초|
|Frequency|미래의 통장 잔고 변화를 파악하고자 할 |
|Concurrency||
|Due Date||
|Etc||

### 2.2.5.분석 및 시뮬레이션

#### 2.2.5.1. Use Case #15 : Simulate Expense

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary|실제 데이터베이스에 저장하지 않는 가상의 지출 이벤트를 임시로 입력하여, 미래의 잔액 변화를 시뮬레이션하는 기능|
|Scope|자금 흐름 스케줄러|
|Level|User Level|
|Author|Lee daeun|
|Last Updated|2026-05-07|
|Status|Analysis|
|Primary Actor|User|
|Preconditions|기본 잔액이 존재해야 한다.|
|Trigger|시뮬레이션 메뉴에서 가상 지출액을 입력하고 버튼을 클릭할 때|
|Success Post Condition|실제 DB 변경 없이 임시 연산 결과가 화면에 시각적으로 출력됨|
|Failed Post Condition|가상 지출 연산이 실패하여 결과를 보여주지 못함|

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1|사용자가 가상의 지출 금액과 날짜를 입력한다|
|2|시스템은 실제 데이터베이스에 저장하지 않고 임시로 미래 잔액 변화를 계산한다|
|3|계산된 미래의 잔액 변화 결과를 화면에 출력한다|

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2|2a 시뮬레이션 결과 마이너스 잔액(오버드래프트)이 발생하는 경우 <br>2a.1 해당 날짜와 잔액 부족 상태를 붉은색으로 강조하여 위험 경고를 함께 띄운다|

|RELATED INFORMATION||
|:---|:---|
|Performance|<=10초|
|Frequency|큰 지출을 결정하기 전 자금 흐름을 테스트할 때|
|Concurrency||
|Due Date||
|Etc||

#### 2.2.5.2 Use Case #16 : Check Overdraft Alert

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary|자금 흐름 연산 결과, 예상 잔액이 0원 미만으로 떨어지는 날짜와 부족 금액에 대한 시스템 경고 알림을 띄우는 기능|
|Scope|자금 흐름 스케줄러|
|Level|User Level|
|Author|Lee daeun|
|Last Updated|2026-05-07|
|Status|Analysis|
|Primary Actor|User|
|Preconditions|기존 잔액, 고정 지출이 한개 이상 존재해야 한다|
|Trigger|연산 과정 중 현재로 부터 일주일 내의 잔고가 마이너스로 예측될 때|
|Success Post Condition|위험 날짜와 부족 금액이 명시된 경고 알림이 화면에 노출됨|
|Failed Post Condition|조건을 충족했음에도 경고 알림이 발생하지 않음|

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1|시스템이 타임라인 조회 통해 예상 잔액을 순차적으로 계산한다|
|2|특정 시점에 예상 잔액이 0원 미만으로 떨어지는 것을 감지한다|
|3|위험 날짜와 부족 금액에 대한 시스템 경고 알림을 화면 상단이나 팝업으로 띄운다|

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2|2a 연속된 여러 날짜에 걸쳐 마이너스 잔고가 유지되는 경우 <br>2a.1 최초 발생일을 기준으로 경고를 발생시키고 전체 위험 구간을 강조한다|

|RELATED INFORMATION||
|:---|:---|
|Performance|<=10초|
|Frequency|자금 마이너스 위험이 감지될 때 자동 발생|
|Concurrency||
|Due Date||
|Etc||

#### 2.2.5.3. Use Case #17 : Calculate Depletion Data

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary|등록된 수입 및 고정/유동 지출 타임라인 데이터를 연산하여, 예상 잔액이 0원 이하로 떨어지는 최초의 날짜(자금 고갈 예정일)를 도출하고 제공하는 기능|
|Scope|자금 흐름 스케줄러|
|Level|User Level|
|Author|Lee daeun|
|Last Updated|2026-05-07|
|Status|Analysis|
|Primary Actor|User|
|Preconditions|연산 가능한 수입/지출 데이터가 충분히 입력된 상태여야 한다|
|Trigger|사용자가 '고갈일 분석' 등의 버튼을 눌러 연산을 요청할 때|
|Success Post Condition|자금이 고갈되는 최초 시점의 날짜가 명확하게 보고됨|
|Failed Post Condition|연산 오류로 날짜를 도출하지 못함|

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1|사용자가 현재 소비 패턴 유지 시 파산 시점을 파악하기 위해 분석을 요청한다|
|2|시스템은 등록된 수입 및 지출 타임라인 데이터를 시간순으로 연산한다|
|3|예상 잔액이 최초로 0원 이하로 떨어지는 날짜(자금 고갈 예정일)를 찾아 제공한다|

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2|2a 최대 연산 범위(사용자 설정)까지 잔액이 0원 이하로 떨어지지 않는 경우 <br>2a.1 "해당 기간 내 자금 고갈 위험이 없습니다"라는 메시지를 출력한다|

|RELATED INFORMATION||
|:---|:---|
|Performance|<=10초|
|Frequency|소비 패턴 유지에 따른 잔액 부족 시점을 정확히 알고자 할 때|
|Concurrency||
|Due Date||
|Etc||

### 2.2.6. 시스템 관리

#### 2.2.6.1. Use Case #18 : Save Data

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary|입력 및 변경된 모든 금융 이벤트 데이터를 로컬 파일에 영구 저장(Persistence)하는 기능|
|Scope|자금 흐름 스케줄러|
|Level|System Level/User Level|
|Author|Lee daeun|
|Last Updated|2026-05-07|
|Status|Analysis|
|Primary Actor|User|
|Preconditions|시스템에 신규 등록되거나 변경된 금융 이벤트 데이터가 존재해야 한다|
|Trigger|사용자가 저장버튼을 클하거나 프로그램을 정상 종료할 때|
|Success Post Condition|데이터가 로컬 파일 등에 영구 저장되어 재실행 시 복구가 가능해짐|
|Failed Post Condition|저장 실패로 메모리의 데이터가 휘발될 수 있|

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1|사용자가 저장 버튼을 누르거나 시스템을 정상적으로 종료한다|
|2|시스템은 데이터를 로컬 파일에 저장하여 프로그램 재시작 시에도 유지되도록 처리한다|

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2|2a 시스템의 저장소 권한이 없거나 여유 공간이 부족한 경우 <br>2a.1 "저장소 접근 오류로 데이터를 저장할 수 없습니다"라는 오류 메시지를 즉시 띄운다|

|RELATED INFORMATION||
|:---|:---|
|Performance|<=5|
|Frequency|시스템 정상 종료 시 또는 사용자가 저장 버튼 클릭 시|
|Concurrency||
|Due Date||
|Etc||


# 3. Domain analysis

### 3.1. Control
시스템에서 모든 클래스들이 공통적으로 사용하게 될 메소드들을 포함한 클래스이다.
ex) 화면 이동, 알림 메시지 띄우기, 팝업창 띄우기 등

### 3.2. User
시스템에 가입하여 스케줄러를 사용하게 될 유저들의 계정 정보를 가지는 클래스이다.
ex) ID, Password 등

### 3.3. Financial Data
시스템에 등록되는 수입 및 지출 내역들이 공통적으로 가지는 속성들을 정의하는 부모 클래스이다.
ex) 날짜, 금액, 카테고리 등

### 3.4. Income
Financial Data를 상속받은 클래스로 유저의 수입 내역에 대한 정보를 가지는 클래스이다.
ex) 수입 금액, 수입 날짜 등

### 3.5. Expense
Financial Data를 상속받은 클래스로 유저의 단발성 및 고정 지출 내역에 대한 정보를 가지는 클래스이다.
ex) 지출 금액, 지출 날짜, 결제 주기 등

### 3.6. Account Management
유저의 회원가입, 로그인, 로그아웃 등 계정의 접근 권한을 관리하는 핵심 기능들이 담긴 클래스이다.
ex) ID 중복 확인, 계정 저장, 로그인 인증, 로그아웃 처리 등

### 3.7. Finance Management
유저가 수입, 단발성 지출, 고정 지출 내역을 직접 추가, 수정, 삭제하며 관리하는 핵심 기능들이 통합된 클래스이다.
ex) 수입/지출 내역 추가, 상세 정보 변경, 내역 삭제 등

### 3.8. Timeline
유저가 특정 키워드로 내역을 검색하거나, 기간별 자금 흐름과 일별 누적 잔액을 조회하는 기능이 담긴 클래스이다.
ex) 조건 필터링, 날짜순 정렬, 누적 예상 잔액 연산 등

### 3.9. Analysis & Simulation
가상 지출 입력에 따른 미래 잔액 시뮬레이션 및 자금 고갈일 예측 기능이 담긴 클래스이다.
ex) 가상 지출 연산, 시뮬레이션 결과 출력, 자금 고갈 예정일 계산

### 3.10. Alert
자금 흐름 연산 중 잔액이 0원 미만으로 떨어질 경우를 감지하여 사용자에게 알림을 띄우는 기능이 담긴 클래스이다.
ex) 마이너스 잔고 감지, 위험 날짜 및 부족 금액 팝업 출력 등

### 3.11. File Management
시스템에 입력 및 변경된 모든 금융 이벤트 데이터를 로컬 파일에 영구 저장하고 복구하는 기능이 담긴 클래스이다.
ex) 로컬 데이터 저장, 프로그램 재시작 시 데이터 불러오기 등

# 4. User Interface prototype

# 5. Glossary

# 6. References
