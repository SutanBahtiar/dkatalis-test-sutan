build-bank:
	@cd bank; mvn clean install;
start-bank:
	@java -jar bank/target/bank-1.0.0-RELEASE.jar
build-atm:
	@cd atm; mvn clean install;
start-atm:
	@java -jar atm/target/atm-1.0.0-RELEASE.jar


.PHONY: build-bank start-bank build-atm start-atm
