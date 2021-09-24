<!-- PROJECT SHIELDS -->
[![LinkedIn][linkedin-shield]][linkedin-url]


## About The Project
Command Line Interface (CLI) to simulate an interaction of an ATM with a retail bank.

[Test case](ATM.md#section)


### Bank Project
The [Bank](bank/README.md#section) acts as the recipient of command from atm and saves the data.


### ATM Project
The [ATM](atm/README.md#section) acts as give the [command](ATM.md#section) to bank.


## How to start
1. Build bank
```sh
make build bank
```
2. Start bank
```sh
make start bank
```
3. Build atm
```sh
make build atm
```
3. Start atm
```sh
make start atm
```

## License
Distributed under the Apache License 2.0. See [LICENSE](LICENSE#section) for more information.


## Contact
Sutan Bahtiar - [LinkedIn](https://www.linkedin.com/in/sutan-bahtiar-97026735) - sutan.bahtiar@gmail.com

<!-- MARKDOWN LINKS & IMAGES -->
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=flat-square&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/sutan-bahtiar-97026735
