# String Wizard Plugin for Android Studio

String Wizard is an Android Studio plugin designed to simplify working with string resources in Android projects. It provides a set of tools for managing string files efficiently, including copying strings between modules, sorting string files, and adding new strings manually or from an Excel file.

## Features

- Copy Strings Between Modules  
  Easily copy string resources from one module to another, ensuring consistent localization across your project.

- Sort String Files  
  Automatically sort all string resources within a module to maintain organization and readability.

- Add New Strings  
  Quickly add a new string to a module with default values for specified languages (e.g., ru, en).

- Import Strings from Excel  
  Import multiple strings directly from an Excel file to populate your string resources efficiently.

## Technologies Used

- Kotlin: Plugin core written in Kotlin for modern and concise code.
- IntelliJ Swing Components: Used for creating a rich, user-friendly UI.
- Apache POI: Library for reading and writing Excel files.

## Installation

1. Download the plugin .jar file from the [releases page](#).
2. Open Android Studio and navigate to File > Settings > Plugins (or Preferences > Plugins on macOS).
3. Click on Install Plugin from Disk and select the downloaded .jar file.
4. Restart Android Studio to activate the plugin.

## Usage

Once installed, you can find the plugin's actions under the Tools > String Wizard menu.

1. Copy Strings Between Modules
    - Select the source and target modules from the UI.
    - Click the "Copy" button to transfer string resources.

   ![Placeholder for copy strings screenshot](#)

2. Sort String Files
    - Open the target module.
    - Select "Sort String Files" from the plugin menu.

   ![Placeholder for sort strings screenshot](#)

3. Add New Strings
    - Enter the string name and values for different languages in the provided fields.
    - Click "Add" to save the string in the selected module.

   ![Placeholder for add new string screenshot](#)

4. Import Strings from Excel
    - Select an Excel file containing string data.
    - Map the columns to resource keys and values.
    - Click "Import" to populate the strings.

   ![Placeholder for import strings screenshot](#)

## Screenshots

- Placeholder for general UI screenshot
- Placeholder for add string UI screenshot
- Placeholder for import strings UI screenshot

## Development

This plugin was developed using the IntelliJ Platform SDK. The following libraries and tools were used:

- Kotlin for the main logic.
- IntelliJ Swing Components for UI development.
- Apache POI for Excel file processing.

To build and modify the plugin, clone this repository and open it in IntelliJ IDEA with the Plugin Development SDK configured.

## Contribution

Contributions are welcome! If you encounter any bugs or have feature requests, please open an issue or submit a pull request. 😊
