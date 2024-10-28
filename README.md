# CSE-360-Project-1-Group-Th23

## Group Members
- Ayush Kaushik (Team Leader)
- Jaafar Abdeen
- Lewam
- Aaryan
- Pragya

# Project Setup Instructions

To ensure that the project works as expected, please follow the steps below to set up the required dependencies and organize them in the appropriate locations.

## Prerequisites

- **Java Development Kit (JDK) 23.0.1**: Install the latest compatible JDK version (23.0.1 or newer).
- **JavaFX SDK 23.0.1**: Download the JavaFX SDK and add it to the `Modulepath`.
- **BouncyCastle 2.73.6** and **H2 Database 2.3.232**: Download these libraries and add them to the `Classpath`.

## Steps

1. **Set Up the JavaFX SDK in the Modulepath**
   - Download the **JavaFX SDK** (version 23.0.1) from the [official website](https://openjfx.io).
   - Add the JavaFX SDK to the **Modulepath** in your IDE:
     - Go to your project settings or module settings.
     - Find the **Modulepath** section and add the JavaFX library files to it.

2. **Set Up the JRE System Library**
   - Ensure your project is using **Java SE 23.0.1** as the JRE System Library.
   - You can verify this in your IDE's project settings or module settings.

3. **Add BouncyCastle and H2 Libraries to the Classpath**
   - Download **BouncyCastle 2.73.6** from [the official BouncyCastle website](https://www.bouncycastle.org).
   - Download **H2 Database 2.3.232** from [the H2 Database website](https://h2database.com).
   - Add both libraries to the **Classpath**:
     - Go to your project settings or module settings.
     - Locate the **Classpath** section and add both library files to it.

## Expected Structure

After setting up, your project structure should look like this:

```
Modulepath
├── javafx
├── JRE System Library [Java SE 23.0.1]
Classpath
├── BouncyCastle
└── H2
```

Following this setup will ensure that all dependencies are properly linked and organized, allowing the project to compile and run without issues.
