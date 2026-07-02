GROUP 19
Masakgramprompt project database

Our SQL database is hosted online using Railway's cloud deployment service. 
This enabled simultaneous and concurrent access to the original database 
for all team members. 
Our cloud-hosted database will lose service and shut down on 25 July 2026 
as per Railway's free-tier restrictions. Therefore, the lecturer is provided 
2 options to evaluate our project and ensure the database is functional.

DATABASE SETUP INSTRUCTIONS
============================

OPTION 1: Import locally
1. Install MySQL
2. Open MySQL Workbench
3. Go to Server → Data Import
4. Select the provided masakgramprompt_dump.sql file
5. Click Start Import
6. Update the project's application.properties found in DAD_Project/nutritional-llm-service/src/main/resources/:
   spring.datasource.url=jdbc:mysql://localhost:3306/masakgramprompt
   spring.datasource.username=root
   spring.datasource.password=YOUR_LOCAL_PASSWORD

OPTION 2: Connect to our live Railway database before 25 July 2026
   Host:     switchback.proxy.rlwy.net
   Port:     21028
   User:     root
   Password: PXDRomZJbmCRXJBOMWKVuSawxmDFVvsY
   Database: masakgramprompt
   
   Update application.properties with these credentials
   (already set by default in submitted application.properties)