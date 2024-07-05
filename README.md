# Large file reading challenge

Write an application that, at the endpoint specified by you, returns the yearly average temperatures for a given city in the format array of objects with the following fields: year, averageTemperature.

## Assumptions

- CSV file with data is no less than 3GB in size.
- The file represents temperature measurements in the format city;yyyy-mm-dd HH:mm:ss.SSS;temp
- The content of the source file may change during the application's running

## Example source file
[example_file.csv](inputs/example_file.csv)


## Example response
```json
[
  {
	"year": "2021",
	"averageTemperature": 12.1
  },
  {
	"year": "2022",
	"averageTemperature": 11.1
  },
  {
	"year": "2023",
	"averageTemperature": 14.1
  }
]
```

## Requirements
- Source code should be placed in a public repository (e.g. GitHub, GitLab, Bitbucket)

## Proposed Solution

### Understanding the requirements

As part of the implementation, our input data entry will be in CSV file format, and our server needs to be able to track runtime changes done to the file.

This will include:

- Collecting data from the file and storing it in a database
- Triggering runtime changes in real-time to update the data
- Developing business logic for querying the appropriate results for the user
- Adding tests for global business rules to ensure comprehensive code coverage

### Proposed Solution

The following diagram represents the proposed technical architecture of the solution. It consists of the following components:

- **Apache Camel**: Acting as an integration framework, Apache Camel will integrate the file-watch connector to sync CSV file updates.
- **Spring Batch**: It will persist data in batch and load the content of the CSV file during application startup.
- **JGit**: It will be used to calculate the difference between the changes done to the CSV file.
- **Spring Boot**: The application will be wrapped in a Spring Boot application, enabling the definition of APIs with web dependencies.
- **Database**: PostgreSQL for managing data, regardless of the dataset size. PostgreSQL is free, open-source, and highly extensible. It supports features for managing large volumes of data, such as partitioning for big tables. Special PostgreSQL modules will be installed to boost database performance, and the server will be hosted in a Docker container.
- **Testing**: Unit/integration tests based on JHipster Lite prototypes, including dependencies such as JUnit, Mockito, and Cucumber.

![img.png](documentation/arch.png)