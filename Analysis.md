# 자금 흐름 스케줄러 Analysis
22421674 이다은 rosedaeuni@gmail.com

### revision history
| date | version | description | author |
|:---|:---|:---|:---|
| 2026.05.07 | v1.0 | 초안 | 이다은 |

# 1. Introduction

### 1.1, Summary

자동이체, OTT 등 구독 시스템이 늘어나며 날짜 기반의 고정지출이 증가함에 따라 대학생 및 사회초년생들은 제한된 수입 내에서 다음 주나 다음 달 발생할 수 있는 일시적인 잔액 부족 상황을 미리 파악하고 대처하기 어렵다. 기존의 가계부 앱들은 과거의 소비를 기록하는데에 그치기 떄문에 미래의 자금 흐름을 파악하고 이에 대비하는 데에 한계가 있다.
따라서 이를 해결하기위해 수입과 지출 예정일을 기반으로 미래의 자금 흐름을 시각화 하는 예측형 금융 스케줄링 시스템인 "자금 흐름 스케줄러"를 만들게 되었다.

### 1.2. Business Goals

"자금 흐름 스케줄러"의 주요 목적은 정기적으로 발생하는 고정지출과 불규칙 적인 반발성 지출을 하나의 타임라인으로 통합하여 미래의 예상 잔액을 도출하는 것이다. 특히 통장 잔고가 0원 이하로 떨어지는 예정일을 사전에 경고함으로써 사용자의 재무적 위험을 방지하고 계획적인 소비를 돕는다.

### 1.3. Technical Goals

- 서버에 회원 정보, 자금흐름 등 관련 정보를 저장하게 하고, 이 정보들은 개인의 것이기에 본안만 수정할 수 있게 한다

- 유저는 회원가입/로그인이 가능하고, 로그인을 해야 해당 정보에 접근할 수 있게 한다.

- 기능을 실행할 때 결과를 빠르게 보여줄 수 있도록 적합한 알고리즘을 선택한다.


# 2. Use case analysis

### 2.1. Use case diagram


### 2.2. Use case description

#### 2.2.1. Use Case #1 : Join

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary||
|Level||
|Author||
|Last Updated||
|Status||
|Primary Actor||
|Preconditions||
|Trigger||
|Success Post Condition||
|Failed Post Condition||

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1||
|2||
|3||

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2||

|RELATED INFORMATION||
|:---|:---|
|Performance||
|Frequency||
|Concurrency||
|Due Date||
|Etc||

#### 2.2.2. Use Case #2 : Login

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary||
|Level||
|Author||
|Last Updated||
|Status||
|Primary Actor||
|Preconditions||
|Trigger||
|Success Post Condition||
|Failed Post Condition||

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1||
|2||
|3||

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2||

|RELATED INFORMATION||
|:---|:---|
|Performance||
|Frequency||
|Concurrency||
|Due Date||
|Etc||

#### 2.2.3. Use Case #3 : Logout

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary||
|Level||
|Author||
|Last Updated||
|Status||
|Primary Actor||
|Preconditions||
|Trigger||
|Success Post Condition||
|Failed Post Condition||

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1||
|2||
|3||

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2||

|RELATED INFORMATION||
|:---|:---|
|Performance||
|Frequency||
|Concurrency||
|Due Date||
|Etc||

#### 2.2.4. Use Case #4 : Add Income

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary||
|Level||
|Author||
|Last Updated||
|Status||
|Primary Actor||
|Preconditions||
|Trigger||
|Success Post Condition||
|Failed Post Condition||

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1||
|2||
|3||

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2||

|RELATED INFORMATION||
|:---|:---|
|Performance||
|Frequency||
|Concurrency||
|Due Date||
|Etc||

#### 2.2.5. Use Case #5 : Modify Income

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary||
|Level||
|Author||
|Last Updated||
|Status||
|Primary Actor||
|Preconditions||
|Trigger||
|Success Post Condition||
|Failed Post Condition||

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1||
|2||
|3||

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2||

|RELATED INFORMATION||
|:---|:---|
|Performance||
|Frequency||
|Concurrency||
|Due Date||
|Etc||

#### 2.2.6. Use Case #6 : Delete Income

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary||
|Level||
|Author||
|Last Updated||
|Status||
|Primary Actor||
|Preconditions||
|Trigger||
|Success Post Condition||
|Failed Post Condition||

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1||
|2||
|3||

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2||

|RELATED INFORMATION||
|:---|:---|
|Performance||
|Frequency||
|Concurrency||
|Due Date||
|Etc||

#### 2.2.7. Use Case #7 : Delete Income

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary||
|Level||
|Author||
|Last Updated||
|Status||
|Primary Actor||
|Preconditions||
|Trigger||
|Success Post Condition||
|Failed Post Condition||

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1||
|2||
|3||

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2||

|RELATED INFORMATION||
|:---|:---|
|Performance|||
|Frequency||
|Concurrency||
|Due Date||
|Etc||

#### 2.2.8. Use Case #8 : Add Expense

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary||
|Level||
|Author||
|Last Updated||
|Status||
|Primary Actor||
|Preconditions||
|Trigger||
|Success Post Condition||
|Failed Post Condition||

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1||
|2||
|3||

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2||

|RELATED INFORMATION||
|:---|:---|
|Performance||
|Frequency||
|Concurrency||
|Due Date||
|Etc||

#### 2.2.9. Use Case #9 : Modify Expense

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary||
|Level||
|Author||
|Last Updated||
|Status||
|Primary Actor||
|Preconditions||
|Trigger||
|Success Post Condition||
|Failed Post Condition||

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1||
|2||
|3||

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2||

|RELATED INFORMATION||
|:---|:---|
|Performance|||
|Frequency||
|Concurrency||
|Due Date||
|Etc||

#### 2.2.10. Use Case #10 : Delete Expense

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary||
|Level||
|Author||
|Last Updated||
|Status||
|Primary Actor||
|Preconditions||
|Trigger||
|Success Post Condition||
|Failed Post Condition||

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1||
|2||
|3||

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2||

|RELATED INFORMATION||
|:---|:---|
|Performance||
|Frequency||
|Concurrency||
|Due Date||
|Etc||

#### 2.2.11. Use Case #11 : Search Financial Event

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary||
|Level||
|Author||
|Last Updated||
|Status||
|Primary Actor||
|Preconditions||
|Trigger||
|Success Post Condition||
|Failed Post Condition||

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1||
|2||
|3||

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2||

|RELATED INFORMATION||
|:---|:---|
|Performance||
|Frequency||
|Concurrency||
|Due Date||
|Etc||

#### 2.2.12. Use Case #12 : View Projected Balance

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary||
|Level||
|Author||
|Last Updated||
|Status||
|Primary Actor||
|Preconditions||
|Trigger||
|Success Post Condition||
|Failed Post Condition||

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1||
|2||
|3||

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2||

|RELATED INFORMATION||
|:---|:---|
|Performance||
|Frequency||
|Concurrency||
|Due Date||
|Etc||

#### 2.2.13. Use Case #13 : Simulate Expense

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary||
|Level||
|Author||
|Last Updated||
|Status||
|Primary Actor||
|Preconditions||
|Trigger||
|Success Post Condition||
|Failed Post Condition||

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1||
|2||
|3||

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2||

|RELATED INFORMATION||
|:---|:---|
|Performance||
|Frequency||
|Concurrency||
|Due Date||
|Etc||

#### 2.2.14. Use Case #14 : Check Overdraft Alert

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary||
|Level||
|Author||
|Last Updated||
|Status||
|Primary Actor||
|Preconditions||
|Trigger||
|Success Post Condition||
|Failed Post Condition||

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1||
|2||
|3||

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2||

|RELATED INFORMATION||
|:---|:---|
|Performance||
|Frequency||
|Concurrency||
|Due Date||
|Etc||

#### 2.2.15. Use Case #15 : Save Data

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary||
|Level||
|Author||
|Last Updated||
|Status||
|Primary Actor||
|Preconditions||
|Trigger||
|Success Post Condition||
|Failed Post Condition||

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1||
|2||
|3||

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2||

|RELATED INFORMATION||
|:---|:---|
|Performance||
|Frequency||
|Concurrency||
|Due Date||
|Etc||

#### 2.2.16. Use Case #16 : Add Fixed Expense

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary||
|Level||
|Author||
|Last Updated||
|Status||
|Primary Actor||
|Preconditions||
|Trigger||
|Success Post Condition||
|Failed Post Condition||

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1||
|2||
|3||

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2||

|RELATED INFORMATION||
|:---|:---|
|Performance||
|Frequency||
|Concurrency||
|Due Date||
|Etc||

#### 2.2.17. Use Case #17 : Modify Fixed Expense

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary||
|Level||
|Author||
|Last Updated||
|Status||
|Primary Actor||
|Preconditions||
|Trigger||
|Success Post Condition||
|Failed Post Condition||

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1||
|2||
|3||

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2||

|RELATED INFORMATION||
|:---|:---|
|Performance||
|Frequency||
|Concurrency||
|Due Date||
|Etc||

#### 2.2.18. Use Case #18 : Delete Fixed Expense

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary||
|Level||
|Author||
|Last Updated||
|Status||
|Primary Actor||
|Preconditions||
|Trigger||
|Success Post Condition||
|Failed Post Condition||

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1||
|2||
|3||

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2||

|RELATED INFORMATION||
|:---|:---|
|Performance||
|Frequency||
|Concurrency||
|Due Date||
|Etc||

#### 2.2.19. Use Case #19 : Calculate Depletion Data

|GENERAL CHARACTERISTICS||
|:---|:---|
|Summary||
|Level||
|Author||
|Last Updated||
|Status||
|Primary Actor||
|Preconditions||
|Trigger||
|Success Post Condition||
|Failed Post Condition||

|MAIN SUCCESS SCENARIO||
|:---|:---|
|Step|Action|
|1||
|2||
|3||

|EXTENSION SCENARIO||
|:---|:---|
|Step|Branching Action|
|1||
|2||

|RELATED INFORMATION||
|:---|:---|
|Performance||
|Frequency||
|Concurrency||
|Due Date||
|Etc||

# 3. Domain analysis

# 4. User Interface prototype

# 5. Glossary

# 6. References
