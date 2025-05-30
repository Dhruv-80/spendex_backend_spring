# SpendEX: Personal Finance Tracker

## 1. Overview

SpendEX is a Spring Boot application designed to track personal finances.  It allows users to upload PDF transaction statements, extract transaction data, and then analyze spending patterns. The application utilizes a PostgreSQL database to store transaction information and provides APIs for data retrieval and analysis.  Key features include spending categorization, top merchant identification, and tracking spending over time.

## 2. Project Structure

The project follows a standard Maven structure:

* **src/main/java/org/example/spendex:** Contains the application's Java source code.
    * **controller:** Contains the `TransactionController` which handles REST API requests.
    * **model:** Contains the `Transaction` entity class representing a single financial transaction.
    * **repository:** Contains the `TransactionRepository` which provides data access to the database using Spring Data JPA.  It includes custom queries for generating spending reports.
    * **service:** Contains the `PdfTransactionExtractorService` responsible for extracting transaction data from uploaded PDF files using the Tabula-java library.
* **src/main/resources:** Contains application configuration files.
    * **application.properties:** Configures the application, including database connection details and Flyway migration settings.
    * **db/migration:** Contains Flyway SQL migration scripts for database schema management. `V1__create_transaction_table.sql` creates the `transaction` table, and `V2__update_transaction_type.sql` likely adds or modifies a `transactionType` column.
* **pom.xml:**  The project's Maven configuration file, defining dependencies and build settings.
* **mvnw, mvnw.cmd:** Maven wrapper scripts for executing Maven commands.
* **.gitattributes:** Git configuration file specifying line endings for different file types.


## 3. Installation

1. **Prerequisites:** Ensure you have Java 17 and Maven installed.  PostgreSQL should be installed and running on your system.  Create a database named `spendex` with a user `dhruv` and password `Dhruv@2003` (or adjust `application.properties` accordingly).
2. **Clone the repository:** `git clone <repository_url>`
3. **Build the application:** Navigate to the project directory and run `./mvnw clean install`.

## 4. Usage

1. **Run the application:**  Use `./mvnw spring-boot:run` to start the Spring Boot application.
2. **Upload a PDF:** Use a tool like Postman or curl to POST a PDF file to the `/api/transactions/upload` endpoint.  The PDF should contain a tabular representation of transaction data.
3. **Access APIs:** Use the API endpoints described below to retrieve and analyze data.

## 5. API Endpoints

* **`GET /api/transactions`**: Retrieves a list of all transactions.
* **`POST /api/transactions`**: Adds a new transaction (manual entry).
* **`POST /api/transactions/upload`**: Uploads a PDF file containing transactions for processing.
* **`GET /api/transactions/spend-by-category`**: Retrieves total spending grouped by category. (Partial implementation shown)
* **Hypothetical Endpoints (Based on Repository Methods):**
    * `GET /api/transactions/top-merchants`: Retrieves the top 5 merchants based on total spending.
    * `GET /api/transactions/spend-over-time`: Retrieves total spending over time.
    * `GET /api/transactions/net-amount`: Retrieves the net amount (total debit - total credit).

## 6. Contributing

Contributions are welcome! Please open an issue or submit a pull request.  Follow standard Git workflow.

## 7. License

[Specify License here]

## 8. Contact

[Specify contact information here]

## 9. Acknowledgements

* **Spring Boot:** The core framework for the application.
* **Spring Data JPA:** For database interaction.
* **PostgreSQL:** The database system used.
* **Tabula-java:** For PDF table extraction.
* **Flyway:** For database migrations.
* **Apache PDFBox (inferred):** Likely used by `PdfTransactionExtractorService` for PDF handling (although only `PDDocument.load` is visible in the provided snippet, other methods from this library are likely required for proper PDF processing).


