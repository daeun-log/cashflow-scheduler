# 자금 흐름 스케줄러 Design
22421674 이다은 rosedaeuni@gmail.com

### revision history

|date | version | description | author|
|:---|:---|:---|:---|
|2026-06-04|v1.0|초안|이다은|
|2026-06-07|v2.0|안드로이드 앱(MVVM 아키텍처 및 Room DB) 구조로 설계 전면 개편|이다은|
|2026-06-10|v3.0|월별 타임라인 네비게이션, 잔액 조정 내역, 필터 기반 검색, 상세보기 팝업, Expense memo 필드 추가 등 기능 전면 개정|이다은|

## 1. Introduction

### 1.1 Summary & Motivation

고정 지출(OTT 구독료, 통신비 등)의 다변화와 불규칙한 유동 지출로 인해 대학생 및 사회 초년생들은 일시적인 잔액 부족 상황을 사전에 파악하기 어렵습니다. 기존 가계부 프로그램들이 과거 소비 기록에 치중하는 한계를 극복하기 위해, 본 시스템은 미래의 자금 흐름을 월별로 시각화하고 잔액 부족 위험을 사전에 방지하는 '예측형 금융 스케줄링 시스템' 구축을 목적으로 합니다.

본 문서는 분석(Analysis) 단계에서 정의된 요구사항을 실제 소스코드 수준으로 구체화하기 위한 설계(Design) 문서입니다. 본 시스템은 독립성을 확보하고 사생활 정보 유출을 차단하기 위해 외부 DB 서버 없이 안드로이드 내장 Room Database를 활용한 로컬 단독 실행 환경의 안드로이드 앱으로 설계되었으며, MVVM(Model-View-ViewModel) 아키텍처 패턴을 적용하여 UI와 비즈니스 로직을 명확히 분리합니다.

### 1.2 Important Points of Design

* 정확한 월별 잔액 연산: 조회 월의 시작 잔액은 직전 월 말까지의 모든 내역을 누적 합산하여 계산하며, 월별 일수 변동(28~31일) 및 윤년 계산 오차를 Java의 `LocalDate` API로 원천 차단합니다.
* 잔액 조정 내역의 명시적 관리: 실제 잔액과 계산 잔액 간의 차이를 보정할 때, 차액을 잔액조정 카테고리의 수입 또는 지출 내역으로 타임라인에 명시적으로 추가하여 기존 예상 잔액 흐름을 유지하면서 현재 잔액을 정확히 보정합니다.
* 성능 최적화: 월별 타임라인 조회 시 Room DB의 범위 쿼리(getIncomesByRange, getExpensesByRange)로 해당 월 데이터만 선별적으로 로드하고, Java 기본 TimSort 기반의 날짜순 정렬을 적용하여 데이터 누적에 따른 성능 저하를 방지합니다.
* 데이터 영속성 및 사생활 보호: Room Database와 DAO를 통해 금융 데이터를 안드로이드 로컬 SQLite에 안전하게 영구 저장하며, DB 스키마 변경 시 Migration을 통해 기존 데이터를 안전하게 유지합니다.



## 2. Class Diagram
<img width="8192" height="6288" alt="classdiagram" src="https://github.com/user-attachments/assets/b5e22a28-5be2-44b5-b813-11945119cced" />

### 2.1 Class Specifications

#### 2.1.1. View (Activity / Fragment)

|Attributes|
|:---|
|없음 (안드로이드 UI 컴포넌트 공통 인터랙션 정의)|
|Methods|
|+ navigateTo(destination: Class<?>): void : 요청된 화면(로그인, 메인 대시보드, 내역 추가 등)으로 전환 제어 (FragmentManager 또는 Intent 활용)
<br>+ showToast(msg: String): void : 화면 하단에 간단한 안내 메시지 출력
<br>+ showDialog(type: String, msg: String): void : 삭제 확인, 로그아웃 확인 등 중요 동작에 대한 모달 다이얼로그 출력|
|Description|
|시스템 내 모든 UI 클래스(Activity, Fragment)들이 화면 이동, 메시지 출력 등 사용자와의 시각적 인터랙션을 공통적으로 처리할 수 있도록 규정하는 최상위 뷰 컴포넌트입니다. ViewModel을 구독(observe)하여 LiveData의 변경 사항을 UI에 반영합니다.|



#### 2.1.2. User

|Attributes|
|:---|
|- userId: String : 사용자의 고유 식별자 계정 ID (PrimaryKey)
<br>- userPw: String : 유저 로그인 인증용 암호|
|Methods|
|+ getUserId(): String
<br>+ getUserPw(): String
<br>+ setUserId(id: String): void
<br>+ setUserPw(pw: String): void|
|Description|
|시스템에 등록되어 고유 금융 스케줄 데이터를 소유하고 접근 권한을 획득하는 사용자 계정 주체를 정의하는 Room Entity Model 클래스입니다. userId를 PrimaryKey로 사용하여 DB 레벨에서 중복 가입을 방지합니다.|



#### 2.1.3. Income

|Attributes|
|:---|
|+ id: int : Room이 자동 생성하는 고유 PK (autoGenerate)
<br>+ userId: String : 어떤 유저의 데이터인지 연결하는 외래키 역할
<br>+ date: String : 수입 발생(예정)일 (YYYY-MM-DD 포맷)
<br>+ amount: int : 수입 금액
<br>+ category: String : 수입 분류 카테고리 (예: 월급, 용돈, 잔액조정)
<br>+ source: String : 수입 출처 및 상세내용 메모|
|Methods|
|+ getId(): int / + setId(id: int): void
<br>+ getUserId(): String / + setUserId(userId: String): void
<br>+ getDate(): String / + setDate(date: String): void
<br>+ getAmount(): int / + setAmount(amount: int): void
<br>+ getCategory(): String / + setCategory(category: String): void
<br>+ getSource(): String / + setSource(source: String): void|
|Description|
|타임라인 연산 시 누적 잔액을 가산하는 양(+)의 금융 이벤트를 나타내는 Room Entity 클래스입니다. 잔액 조정 시 생성되는 잔액조정 수입 내역도 카테고리="잔액조정", source=변경원인으로 이 테이블에 저장됩니다.|



#### 2.1.4. Expense

|Attributes|
|:---|
|+ id: int : Room이 자동 생성하는 고유 PK (autoGenerate)
<br>+ userId: String : 유저 연결
<br>+ date: String : 지출 발생(예정)일 (YYYY-MM-DD 포맷)
<br>+ amount: int : 지출 금액
<br>+ category: String : 지출 분류 카테고리 (예: 식비, 구독, 잔액조정)
<br>+ memo: String : 지출 상세내용 메모
<br>+ isFixed: boolean : 정기적 고정 지출 여부 (true: 고정, false: 일회성)
<br>+ cycle: int : 고정 지출의 반복 주기 (월 단위, 일회성이면 0)|
|Methods|
|+ getId(): int / + setId(id: int): void
<br>+ getUserId(): String / + setUserId(userId: String): void
<br>+ getDate(): String / + setDate(date: String): void
<br>+ getAmount(): int / + setAmount(amount: int): void
<br>+ getCategory(): String / + setCategory(category: String): void
<br>+ getMemo(): String / + setMemo(memo: String): void
<br>+ isFixed(): boolean / + setFixed(isFixed: boolean): void
<br>+ getCycle(): int / + setCycle(cycle: int): void|
|Description|
|일회성 단발성 지출과 매월 정기 반복 발생하는 고정 지출(구독료 등)의 속성을 isFixed 플래그와 cycle 주기로 통합 관리하는 Room Entity 클래스입니다. v2(DB version 2)에서 memo 컬럼이 추가되었으며, MIGRATION_1_2를 통해 기존 데이터를 안전하게 유지합니다.|



#### 2.1.5. AccountViewModel & AccountRepository

|Attributes|
|:---|
|- currentUser: MutableLiveData\<User\> : 현재 인증에 성공하여 시스템을 점유하고 있는 유저 객체 정보|
|Methods|
|+ checkDuplicateId(id: String): LiveData\<Integer\> : 회원가입 또는 로그인 1단계에서 ID가 Room DB에 이미 존재하는지 비동기 조회
<br>+ joinUser(user: User): void : 신규 사용자 Entity를 백그라운드 스레드에서 Room DB에 Insert하여 계정 등록
<br>+ login(id: String, pw: String): LiveData\<User\> : Room DB에서 ID/PW 일치 레코드를 조회하여 인증 결과를 LiveData로 반환
<br>+ setCurrentUser(user: User): void : 로그인 성공 시 세션 유저 정보 저장
<br>+ logout(): void : 현재 유저 LiveData를 null로 초기화하여 세션을 소멸시키고 로그인 화면으로 전이|
|Description|
|유저의 회원가입 권한 검증, 단계별 로그인 인증(아이디 존재 여부 → 비밀번호 일치 여부), 로그아웃 트랜잭션을 전담하는 MVVM 컴포넌트입니다. AccountViewModel은 UI와 Repository 사이의 데이터 중개자 역할을 하며, AccountRepository는 UserDao를 통해 Room DB에 직접 접근합니다.|



#### 2.1.6. FinanceViewModel & FinanceRepository

|Attributes|
|:---|
|- timelineResult: MutableLiveData\<List\<DayEntry\>\> : 월별 타임라인 연산 결과 (UI에 자동 반영)
<br>- overdraftAlert: MutableLiveData\<String\> : 30일 내 고갈 경고 문자열 (null이면 위험 없음)
<br>- currentBalanceLive: MutableLiveData\<Integer\> : 오늘까지 모든 내역을 합산한 현재 잔액|
|Methods|
|+ addIncome / updateIncome / deleteIncome(income: Income): void : 수입 CRUD를 백그라운드 스레드에서 Room DB에 반영
<br>+ addExpense / updateExpense / deleteExpense(expense: Expense): void : 지출 CRUD를 백그라운드 스레드에서 Room DB에 반영
<br>+ calculateMonth(yearMonth: YearMonth): void : 해당 월의 시작 잔액 계산 후 월별 타임라인을 연산하고, 오늘 이후 30일 내 고갈 여부를 탐지
<br>+ adjustBalance(targetBalance: int, reason: String): void : 계산 잔액과 목표 잔액의 차액을 잔액조정 카테고리의 수입 또는 지출 내역으로 오늘 날짜에 저장
<br>+ calcBalanceUpTo(toDate: String, fromDate: String): int : 특정 기간 내 모든 내역을 합산하여 잔액을 반환하는 내부 헬퍼 메서드|
|Description|
|유저의 입력에 따라 Room DB의 금융 데이터 CRUD를 전담하고, 월별 타임라인 연산 및 잔액 조정 로직을 수행하는 MVVM 핵심 컴포넌트입니다. FinanceViewModel은 UI로부터 사용자 액션을 전달받아 Repository에 위임하며, DB 변경 사항은 LiveData를 통해 UI에 실시간으로 자동 갱신됩니다.|



#### 2.1.7. Timeline

|Attributes|
|:---|
|+ currentBalance: int : 해당 월의 시작 잔액 (직전 월 말 기준으로 계산된 값)
<br>+ incomes: List\<Income\> : 해당 월의 수입 내역 목록
<br>+ oneTimeExpenses: List\<Expense\> : 해당 월의 일회성 지출 내역 목록
<br>+ fixedExpenses: List\<Expense\> : 전체 고정 지출 내역 목록 (해당 월 내 발생하는 것만 전개)
<br>+ fixedEndDate: String : 고정 지출 반복 전개 마감일 (해당 월 말일)
<br>+ projectedTimeline: List\<DayEntry\> : 날짜순 정렬 및 예상 잔액 계산이 완료된 타임라인 데이터|
|Methods|
|+ buildTimelineForMonth(startDate: String, endDate: String): void : 해당 월의 수입/지출/고정지출을 통합하여 날짜순 정렬(TimSort) 후 항목별 누적 예상 잔액을 연산
<br>+ getProjectedTimeline(): List\<DayEntry\> : 연산 완료된 타임라인 반환|
|Description|
|FinanceViewModel에서 전달받은 데이터를 기반으로 월별 자금 흐름 시뮬레이션을 생성하는 핵심 연산 클래스입니다. 고정 지출은 해당 월 내 발생하는 것만 반복 전개하며, 모든 이벤트를 날짜순으로 정렬한 후 현재 잔액에서 순차적으로 증감을 적용하여 항목별 예상 잔액과 마이너스 여부(isDanger)를 산출합니다.|



#### 2.1.8. Timeline.DayEntry

|Attributes|
|:---|
|+ date: String : 금융 이벤트 발생(예정)일 (YYYY-MM-DD)
<br>+ category: String : 카테고리
<br>+ memo: String : 상세내용
<br>+ amount: int : 금액
<br>+ isIncome: boolean : true = 수입, false = 지출
<br>+ isFixed: boolean : true = 고정 지출
<br>+ sourceId: int : 원본 Income 또는 Expense 테이블의 PK
<br>+ isIncomeType: boolean : true = Income 테이블, false = Expense 테이블
<br>+ balanceAfter: int : 이 이벤트 반영 후 누적 예상 잔액
<br>+ isDanger: boolean : balanceAfter < 0 여부|
|Description|
|타임라인 한 줄을 표현하는 데이터 모델 클래스입니다. RecyclerView 어댑터에서 화면 렌더링에 사용되며, 수정/삭제 시 sourceId와 isIncomeType을 통해 원본 레코드를 특정합니다.|



#### 2.1.9. CategoryManager & BalanceManager

|Attributes|
|:---|
|CategoryManager: SharedPreferences prefs : 카테고리 목록(수입/지출/잔액조정 원인)을 영구 저장하는 로컬 저장소
<br>BalanceManager: (현재 버전에서는 제거됨 - 잔액 조정이 타임라인 내역으로 관리되므로 불필요)|
|Methods|
|+ getIncomeCategories(): List\<String\> : 수입 카테고리 목록 반환 (기본값 포함)
<br>+ getExpenseCategories(): List\<String\> : 지출 카테고리 목록 반환 (기본값 포함)
<br>+ getBalanceReasons(): List\<String\> : 잔액 조정 원인 목록 반환 (기본값 포함)
<br>+ addIncomeCategory / addExpenseCategory / addBalanceReason(value: String): void : 새 항목을 목록 맨 앞에 추가하고 즉시 저장|
|Description|
|앱 내에서 사용하는 카테고리 및 잔액 조정 원인 목록을 SharedPreferences에 영구 저장하고 관리하는 유틸리티 클래스입니다. 사용자가 추가한 커스텀 항목이 앱 재시작 후에도 유지됩니다.|



#### 2.1.10. Room Database & DAO (Data Layer)

|Attributes|
|:---|
|- database: AppDatabase : Room 라이브러리가 생성하는 싱글톤 데이터베이스 인스턴스 (SQLite 기반, version 2)
<br>- userDao: UserDao : User Entity의 CRUD 쿼리를 정의하는 DAO 인터페이스
<br>- financeDao: FinanceDao : Income/Expense Entity의 CRUD 및 범위 쿼리를 정의하는 DAO 인터페이스|
|Methods|
|UserDao: + insert(user: User) / + login(id, pw): LiveData\<User\> / + checkDuplicateId(id): LiveData\<Integer\>
<br>FinanceDao: + insertIncome / updateIncome / deleteIncome / getAllIncomes / getIncomesByRange / searchIncomes
<br>FinanceDao: + insertExpense / updateExpense / deleteExpense / getAllExpenses / getExpensesByRange / getFixedExpenses / searchExpenses|
|Description|
|사생활 유출 방지를 위한 로컬 단독 환경 요구사항을 만족하기 위해 Room을 활용하여 데이터의 영속성을 전담합니다. DB version 2에서 Expense 테이블에 memo 컬럼이 추가되었으며, MIGRATION_1_2("ALTER TABLE expenses ADD COLUMN memo TEXT NOT NULL DEFAULT ''")를 통해 기존 데이터를 안전하게 유지합니다. DAO 인터페이스가 Repository를 통해 ViewModel에 데이터를 공급하는 단방향 데이터 흐름을 구성합니다.|



## 3. Sequence Diagram

사용자의 주요 행동에 따른 시스템 내부 객체 간의 제어 흐름과 함수 호출 순서를 정의합니다.

### 3.1 회원가입 및 로그인 (Join & Login)
<img width="1106" height="1384" alt="sd-1" src="https://github.com/user-attachments/assets/dba307fe-e266-43e3-b995-7ddb5dee5bc5" />

* 시나리오 설명: 사용자가 LoginActivity에 자격 증명을 입력하면 AccountViewModel이 AccountRepository에 인증을 위임합니다. 로그인 시 1단계(checkDuplicateId)로 아이디 존재 여부를 먼저 확인하고, 존재할 경우 2단계(login)로 비밀번호 일치 여부를 검증합니다. 각 단계별로 토스트 메시지가 출력되며, 최종 인증 성공 시 userId를 Intent에 담아 MainActivity로 전환합니다.

### 3.2 내역 추가 및 즉시 저장 (Add Event & Room DB Sync)
<img width="1153" height="938" alt="sd-2" src="https://github.com/user-attachments/assets/aacf953d-de78-40e2-ad3e-458462a32b24" />

* 시나리오 설명: 사용자가 내역 추가 화면에서 구분(수입/지출/고정지출), 날짜, 카테고리, 상세내용, 금액을 입력하고 추가 버튼을 누르면 EventEditFragment가 FinanceViewModel에 이벤트를 전달합니다. ViewModel은 FinanceRepository를 통해 백그라운드 스레드(ExecutorService)에서 DAO의 Insert 쿼리를 즉시 실행합니다. Room DB에 저장이 완료되면 getAllIncomes/getAllExpenses의 LiveData가 변경을 감지하여 DashboardFragment의 onResume()을 통해 타임라인이 자동으로 갱신됩니다.

### 3.3 월별 타임라인 연산 (Calculate Monthly Timeline)
<img width="1591" height="1517" alt="sd-3" src="https://github.com/user-attachments/assets/a94a778d-0ccc-4cf8-ae8e-82a7355b9020" />

* 시나리오 설명: 대시보드 진입 또는 월 네비게이터 클릭 시 FinanceViewModel의 calculateMonth(yearMonth)가 호출됩니다. 백그라운드에서 calcBalanceUpTo()를 통해 직전 월 말까지의 누적 잔액(해당 월 시작 잔액)을 산출하고, 해당 월의 수입/지출 내역과 고정 지출을 Room DB에서 조회합니다. Timeline 클래스의 buildTimelineForMonth()가 모든 이벤트를 날짜순 정렬 후 항목별 예상 잔액을 연산하고, 오늘 이후 30일 이내에 마이너스 항목이 존재하면 overdraftAlert LiveData를 통해 경고 배너를 화면 상단에 표시합니다.

### 3.4 잔액 조정 (Adjust Balance)
<img width="1053" height="1241" alt="sd-4" src="https://github.com/user-attachments/assets/f8d7d775-675e-474f-b30e-58e29e3ffcd0" />

* 시나리오 설명: 사용자가 현재 잔액 카드를 클릭하면 잔액 수정 다이얼로그가 열립니다. 목표 잔액과 변경 원인을 입력하고 저장을 요청하면 FinanceViewModel의 adjustBalance()가 호출됩니다. 백그라운드에서 calcBalanceUpTo()로 현재 계산 잔액을 산출하고, 목표 잔액과의 차액이 양수이면 잔액조정 수입 내역(Income)으로, 음수이면 잔액조정 지출 내역(Expense)으로 오늘 날짜에 Room DB에 Insert합니다. LiveData 변경 감지를 통해 타임라인이 자동 갱신되어 현재 잔액이 목표 잔액과 일치하게 됩니다.

### 3.5 내역 상세보기 및 수정/삭제 (View Detail & Modify/Delete Event)
<img width="1216" height="1347" alt="sd-5" src="https://github.com/user-attachments/assets/03ad9bf2-a3d5-436b-b873-dffb84387b0b" />

* 시나리오 설명: 타임라인에서 ⋯ 버튼 클릭 시 상세보기 팝업이 열립니다. 팝업에는 날짜, 구분, 카테고리, 상세내용, 금액이 표시되며, 고정 지출의 경우 향후 4개월 예정 일정도 함께 표시됩니다. 수정 버튼 클릭 시 수정 다이얼로그가 열리고, 변경 완료 후 Room DB Update → LiveData 갱신 → 타임라인 재계산 순으로 처리됩니다. 삭제 버튼 클릭 시 재확인 다이얼로그를 거쳐 Room DB Delete → 타임라인 갱신이 처리됩니다.

### 3.6 데이터 영속화 및 로그아웃 (Room DB Auto-Save & Logout)
<img width="1272" height="1006" alt="sd-6" src="https://github.com/user-attachments/assets/c455ac97-9009-4d87-8f70-4652ca765814" />

* 시나리오 설명: MVVM + Room DB 구조에서는 모든 데이터 변경이 백그라운드 스레드(ExecutorService)를 통해 Room DB에 즉시 반영됩니다. 로그아웃 버튼 클릭 시 확인 팝업을 거쳐 ViewModel의 유저 세션 LiveData를 null로 초기화하고 LoginActivity로 전환합니다. Room의 즉시 저장 방식으로 인해 별도의 종료 시점 저장 절차가 필요하지 않습니다.



## 4. State Machine Diagram

프로그램의 생명주기 동안 사용자 인터페이스의 흐름 변화와 그에 따른 전이(Transition) 조건을 정의합니다.

### 4.1 상태별 상세 가이드 명세

| State ID | State Name | Description (상태 설명) | Transition Trigger (전이 조건) |
| --- | --- | --- | --- |
| 1 | Login / Join Activity | 앱이 기동된 초기 렌더링 화면 상태. AccountViewModel을 통해 Room DB 내 유저 자격 증명을 단계별(아이디 존재 → 비밀번호 일치)로 검증 대기함. | - 로그인 인증 성공 시 → State 2 전이 <br><br> - 회원가입 링크 클릭 시 → State 1a 전이 |
| 1a | Register Activity | 미등록 유저가 계정을 생성하는 화면 상태. AccountViewModel이 Room DB User 테이블의 ID 중복 조회를 비동기로 수행함. | - 중복이 없고 작성 완료 시 → Room DB에 User Entity Insert 후 State 1 전이 |
| 2 | Main Dashboard Fragment | 시스템의 홈 허브 상태. FinanceViewModel의 LiveData를 구독하여 현재 잔액, 월별 타임라인, 30일 내 고갈 경고가 실시간 렌더링됨. 월 네비게이터(< 2026년 6월 >)로 조회 달 전환 가능. | - 내역 추가 버튼 클릭 시 → State 3 전이 <br><br> - 타임라인 항목의 ⋯ 클릭 시 → State 4 전이 <br><br> - 현재 잔액 카드 클릭 시 → State 4a 전이 <br><br> - 로그아웃 버튼 클릭 시 → State 6 전이 |
| 3 | Event Edit Fragment | 수입/지출/고정지출을 통합 등록하는 입력창 상태. 날짜(달력 또는 직접 입력), 구분, 카테고리(기존 선택 또는 신규 추가), 상세내용, 금액을 입력받아 유효성 검사 완료 시 Room DB에 즉시 CRUD 수행. | - 추가 완료 또는 뒤로가기 버튼 클릭 시 → State 2 재진입 (LiveData 자동 갱신) |
| 4 | Event Detail Dialog | 특정 내역의 상세 정보(날짜, 구분, 카테고리, 상세내용, 금액)와 고정지출의 향후 4개월 예정이 표시된 팝업 상태. | - X 버튼 클릭 시 → State 2 복귀 <br><br> - 수정 버튼 클릭 시 → State 4b 전이 <br><br> - 삭제 버튼 클릭 시 → 삭제 확인 다이얼로그 → 삭제 완료 시 State 2 복귀 |
| 4a | Balance Adjust Dialog | 현재 잔액 수정 다이얼로그 상태. 목표 잔액과 변경 원인 입력 후 저장 시, 차액이 잔액조정 내역으로 타임라인에 추가됨. | - 저장 또는 취소 시 → State 2 복귀 |
| 4b | Event Edit Dialog | 기존 내역의 날짜, 금액, 상세내용을 수정하는 다이얼로그 상태. | - 저장 완료 또는 취소 시 → State 2 복귀 (LiveData 자동 갱신) |
| 5 | Search Fragment | 날짜 범위, 카테고리, 상세내역, 내역유형 필터를 조합하여 내역을 검색하는 화면 상태. | - 타임라인 버튼 클릭 시 → State 2 전이 |
| 6 | Logout Confirm Dialog | 로그아웃 확인 팝업 상태. | - 승인 시 → ViewModel 세션 초기화 → State 1 전이 <br><br> - 취소 시 → State 2 복귀 |



## 5. Implementation Requirements

### 5.1 Hardware Requirements

* Device: 안드로이드 스마트폰 또는 태블릿
* Memory (RAM): 2GB 이상 권장
* Storage: 50MByte 이상의 여유 저장 공간

### 5.2 Software Requirements

* OS: Android 8.0 (API Level 26) 이상
* Implementation Language: Java (Android Studio)
* Architecture: MVVM (Model-View-ViewModel)
* Local Database: Room Database (SQLite 기반, version 2)


## 6. Glossary

| Terms (용어) | Description (설명) |
| --- | --- |
| Class Diagram | 객체지향형 소프트웨어 아키텍처 설계에서, 시스템에 참여하는 클래스들의 내부 정적 구조(속성, 메서드)와 상호 간의 관계를 시각적으로 표준화하여 표현한 명세서입니다. |
| Sequence Diagram | 특정 시나리오가 트리거되었을 때, 런타임 상에서 객체들이 시간 순서에 따라 서로 어떤 메시지 호출 및 반환을 교환하는지 묘사하는 행동 모델링 도면입니다. |
| State Machine Diagram | 시스템의 전체 제어 흐름을 하나의 상태 기계로 정의하고, 사용자 입력 이벤트에 따라 화면 및 내부 모드가 어떤 조건을 거쳐 상호 전환되는지 기술하는 명세 다이어그램입니다. |
| MVVM 아키텍처 | Model-View-ViewModel의 약자로, 안드로이드 앱의 UI(View)와 비즈니스 로직(ViewModel/Model)을 명확히 분리하는 아키텍처 패턴입니다. ViewModel이 LiveData를 통해 데이터를 UI에 노출하며, View는 이를 구독(observe)하여 화면을 갱신합니다. |
| Room Database | 안드로이드 공식 로컬 데이터베이스 라이브러리로, SQLite 위에 추상화 계층을 제공합니다. @Entity로 테이블을, @Dao로 쿼리를, @Database로 DB 인스턴스를 정의하며, Migration을 통해 스키마 변경 시 기존 데이터를 안전하게 유지합니다. |
| DAO (Data Access Object) | Room Database에서 SQL 쿼리를 메서드로 추상화하는 인터페이스입니다. @Insert, @Update, @Delete, @Query 어노테이션을 통해 CRUD 작업을 정의하며, Repository가 이를 호출하여 데이터에 접근합니다. |
| LiveData | ViewModel이 View에 데이터를 노출하는 생명주기 인식형 비동기 스트림입니다. Room DB의 변경 사항을 자동으로 감지하여 UI를 실시간으로 갱신합니다. |
| DayEntry | 타임라인 한 줄을 표현하는 데이터 모델 클래스입니다. 날짜, 카테고리, 상세내용(memo), 금액, 구분 플래그(isIncome, isFixed), 원본 PK(sourceId, isIncomeType), 예상 잔액(balanceAfter), 위험 여부(isDanger) 정보를 포함합니다. |
| Balance Adjustment (잔액 조정) | 내역 미입력 등으로 실제 잔액과 계산 잔액 사이에 차이가 발생했을 때, 차액을 잔액조정 카테고리의 수입 또는 지출 내역으로 타임라인에 추가하여 현재 잔액을 보정하는 방식입니다. |
| Monthly Navigator (월 네비게이터) | 대시보드 화면에서 좌우 화살표(< 2026년 6월 >)로 조회 달을 전환하는 UI 컴포넌트로, 선택한 달의 시작 잔액과 타임라인을 자동으로 계산하여 표시합니다. |



## 7. References

* Toss (토스): 소비/지출 및 정기 결제 카테고리 관리 UI (일정 기반 알림 도출 아이디어 및 월별 내역 표시 방식 참고)
* BankSalad (뱅크샐러드): 가계부 자산 타임라인 대시보드 구조 (일회성 유동 지출과 고정 주기의 반복 통합 가공 흐름 설계 참고)
* Whatssub (왓섭): 구독 결제 및 자금 예측 경고 시스템 (결제일 도래 전 전산 오버드래프트 및 고갈 리스크 탐지 로직 벤치마킹)
