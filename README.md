# Smart Pantry Manager
Smart pantry manager is created with Java Android
The application help to track pantry items that you have and gives suggested recipes based on the ingredients you have. The aim is to reduce food waste

## Features

- Add ingredients
- View the current pantry ingredients
- Edit current ingredients
- Delete ingredients
- It saves ingredient name, qty, units and it's expiry date
- You can choose to hide the expiry date from the settings
- Show a list of suggested recipes that you have the ingredients for based off of strict matching
- converts basic singular and plurals for matches in units
- Does basic unit conversion kg to g and litre to ml
- You are able to view the ingredients and steps to make the recipe
- it has persistent local storage

## Recipe Matching
Only recipes that all the ingredients are present for with enough quantity will be suggested
Matching does conversions on compatible units.

## Database
The database is a SQLite database
SQL was selected as it only have to store data locally on the device and it has a small footprint
It can provide persistent storage even after the application is closed
Table include
- Pantry items
- Recipes
- Recipe Ingredients
Full CRUD on the Pantry table

## Technologies Used
- Java
- Android Studio
- XML layouts
- SQLite
- SharedPreferences
- Git and GitHub

## Setup and Run Instructions
1. Clone or download  from my GitHub repository.
2. Open it in **Android Studio**.
3. Wait for Android Studio to finsish with its Gradle sync.
4. Start up an Android emulator to use with the application.
5. Click the **Run** button (play button) in Android Studio.
6. The application will open on the pantry screen with pantry items loaded (seeded to DB).

## Application Screens
The following screens was added to the application

- Pantry
- Add/Edit Ingredient
- Suggested Recipes
- Recipe Detail
- Settings

## Navigation
Androids Intents was used for the navigation from one screen to another screen and to pass information.

## Data Persistence
The data is persisted in the SQLite database but can be reseeded if you change the database version

## Developer
Developed by Marius Swanepoel, 402205594 for Mobile App Development 700.