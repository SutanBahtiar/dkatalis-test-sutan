[![LinkedIn][linkedin-shield]][linkedin-url]


## About The Project
The server to handle commands from [ATM](../atm/readme.md#section) and save the data.


### Commands
`login` `deposit` `withdraw` `transfer` `logout`

## How To Start
1. Build bank `make build bank` or `mvn clean install`
2. Start bank `make start bank` or `java -jar target/bank-1.0.0-SNAPSHOT.jar`

## Structure Project
    .               
    ├── dao                   # dao
    ├── enums                 # enums
    ├── handler               # handler
    ├── helper                # helper
    ├── model                 # model
    ├── util                  # util
    ├── socket                # socket
    └── service               # service

## Structure Data

### Account
| Column Name   | Type   	| Description   |
| ------------- | ---------	| ------------- |
| customerName  | String  	| Customer Name |
| refId         | long      | Reference Id  |
| createdDate   | date   	| Created Date  |

### Entry
| Column Name     | Type		| Description   |
| --------------- |	-------		| ------------- |
| entryId         |	long		| Entry Id      |
| customerName    |	String		| Customer Name |
| transactionCode |	enum TransactionCode		| transaction code: DEPOSIT, WITHDRAW |
| amount          |	double		| Amount        |
| trxId           |	long		| trx Id        |
| refId           |	long		| Reference Id  |
| createdDate     |	date		| Created Date  |

### Transfer
| Column Name     | Type	| Description   |
| --------------- | ------	| ------------- |
| transferId      | long 	| Transfer Id   |
| customerName    | String	| Customer Name |
| toCustomerName  | String	| To Customer Name |
| amount          | double	| Amount        |
| trxId           | long 	| trx Id        |
| refId           | long 	| Reference Id  |
| createdDate     | date 	| Created Date  |

### Transaction
| Column Name     | Type		| Description   |
| --------------- |	-------		| ------------- |
| transactionId   |	long		| Transaction Id|
| customerName    |	String		| Customer Name |
| toCustomerName  | String		| To Customer Name |
| transactionCode |	enum TransactionCode		| transaction code: DEPOSIT, WITHDRAW |
| amount          |	double		| Amount        |
| trxId           |	long		| trx Id        |
| tableId         |	long		| Table Id: entryId, transferId  |
| createdDate     |	date		| Created Date  |

### TransactionCode
| Enum      	|
| -------------	|
| DEPOSIT(0) 	|
| WITHDRAW(1)	|
| TRANSFER(2)	|
| OWED(3)		|

## Diagram Activy

### Login Activity
![](../image/bank-login-activity.png?raw=true)

### Deposit Activity
![](../image/bank-deposit-activity.png?raw=true)

note a create default transaction :
```sh
insert into data entry as deposit
insert into data transaction as deposit
```

note b clear the owed :
```sh
insert into data entry as deposit
insert into data transaction as owed to clear the owed
insert into data transaction as transfer to add balance `toCustomerName`
insert into data transaction as withdraw`(-)` to balancing balance amount `customerName`
```

note b clear the owed and save deposit :
```sh
insert into data entry as deposit
insert into data transaction as owed to clear the owed
insert into data transaction as transfer to add balance `toCustomerName`
insert into data transaction as withdraw`(-)` to balancing balance amount `customerName`
insert into data transaction as deposit`(diff amount deposit and owed)` to add balance `customerName`
```

### Withdraw Activity
![](../image/bank-withdraw-activity.png?raw=true)


### Transfer Activity
![](../image/bank-transfer-activity.png?raw=true)

note a create default transaction :
```sh
insert into data transfer
insert into data transaction as transfer
```

note b clear the owed :
```sh
insert into data transfer
insert into data transaction as owed`(-)` to clear the owed
```

note b clear the owed and create transfer :
```sh
insert into data transfer
insert into data transaction as owed`(-)` to clear the owed
insert into data transaction as transfer`(diff amount transfer and owed)`
```

## License
Distributed under the Apache License 2.0. See [LICENSE](../LICENSE#section) for more information.

## Contact

Sutan Bahtiar - [LinkedIn](https://www.linkedin.com/in/sutan-bahtiar-97026735) - sutan.bahtiar@gmail.com

<!-- MARKDOWN LINKS & IMAGES -->
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=flat-square&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/sutan-bahtiar-97026735
