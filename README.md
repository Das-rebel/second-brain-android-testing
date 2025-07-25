# Second Brain Android

A modern Android application for managing and organizing your digital life, built with Kotlin, Jetpack Compose, and modern Android development practices.

## Features

- **Collection Management**: Create, organize, and manage collections of bookmarks, notes, and other content.
- **Sharing**: Share collections with others with customizable access levels.
- **Offline-First**: Works seamlessly offline with automatic synchronization when online.
- **Modern UI**: Built with Jetpack Compose for a beautiful, responsive user interface.

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose, Material 3
- **Architecture**: MVVM with Clean Architecture
- **Dependency Injection**: Hilt
- **Local Database**: Room
- **Networking**: Retrofit
- **Asynchronous**: Kotlin Coroutines & Flow
- **Dependency Management**: Gradle with version catalogs

## Getting Started

1. Clone the repository
2. Open the project in Android Studio Giraffe or later
3. Build and run the app on an emulator or physical device

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/secondbrain/app/
│   │   │   ├── data/
│   │   │   │   ├── local/dao/     # Room database DAOs
│   │   │   │   ├── model/         # Data models
│   │   │   │   ├── network/       # API service and DTOs
│   │   │   │   ├── repository/    # Repository implementations
│   │   │   │   └── sync/          # Sync manager and related classes
│   │   │   ├── di/                # Dependency injection modules
│   │   │   ├── ui/                # UI components and screens
│   │   │   └── util/              # Utility classes
```

## Contributing

Contributions are welcome! Please read our contributing guidelines before submitting pull requests.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
