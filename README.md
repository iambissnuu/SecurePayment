# SecurePayment

SecurePayment is a small web application created to demonstrate how an online payment system can be protected against SQL injection and cross-site scripting (XSS).

The application allows a user to enter a customer name and payment amount through a web form. The request is processed by a Java backend and the payment information is stored in a MySQL database.

## Security Features

### SQL Injection Protection

The application uses Java `PreparedStatement` with parameterized queries when saving payment information. User input is passed as a parameter instead of being added directly to the SQL statement.

An SQL injection-style input such as:

`' OR '1'='1`

is treated as customer data and stored as a normal value rather than changing the SQL query.

### XSS Protection

SecurePayment uses JavaScript `textContent` when displaying customer information on the page. This prevents user input from being interpreted as HTML.

For example, an XSS test input such as:

`<img src=x onerror=alert('XSS')>`

is displayed as plain text instead of executing JavaScript in the browser.

## Technologies Used

- Java
- JavaScript
- HTML
- CSS
- MySQL
- MySQL Connector/J

## Project Structure

```text
SecurePayment/
├── database/
│   └── setup.sql
├── lib/
│   └── mysql-connector-j-26.7.0.jar
├── src/
│   ├── DatabaseManager.java
│   └── SecurePaymentApp.java
├── web/
│   ├── index.html
│   ├── script.js
│   └── style.css
└── README.md