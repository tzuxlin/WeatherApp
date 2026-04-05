# WeatherApp

A modern, modular Android weather application built with Jetpack Compose, featuring city search, saved locations, and multi-day forecasts, while following Clean Architecture principles.

## Getting Started

1. Clone the repository.
2. Open the project in **Android Studio Ladybug or newer**.
3. Add OpenWeather API key to the project root **`local.properties`** (this file is git-ignored):
   ```properties
   OPENWEATHER_API_KEY=your_key_here
   ```
   The `:core:network` module reads this value at build time into `BuildConfig` and attaches it to API requests via OkHttp.
4. Build and run the `:app` module.

## Features

- **Real-time Weather**: View current weather conditions for any city.
- **Weather Forecast**: Check multi-day forecasts with detailed information.
- **City Search**: Search for cities globally using the OpenWeather Geocoding API.
- **Saved Cities**: Save up to 5 cities for quick access.
- **Responsive UI**: Built entirely with Jetpack Compose for a fluid and modern user experience.
- **Navigation**: Built with Navigation3 for type-safe and flexible navigation.

## Tech Stack

- **UI**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Dependency Injection**: [Hilt](https://dagger.dev/hilt/)
- **Networking**: [Retrofit](https://square.github.io/retrofit/) & [Moshi](https://github.com/square/moshi)
- **Asynchronous Programming**: [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html)
- **Navigation**: [Navigation3](https://developer.android.com/guide/navigation/navigation-3)
- **Architecture**: Clean Architecture with MVI (Model-View-Intent) pattern in the UI layer.
- **Image Loading**: [Coil](https://coil-kt.github.io/coil/)

## Project Structure

The project is highly modularized to ensure separation of concerns and scalability:

- `:app`: The entry point of the application, orchestrating navigation and global DI.
- `:feature:weather`: UI and ViewModels for displaying weather details and saved locations.
- `:feature:city`: UI and ViewModels for city search functionality.
- `:core:domain`: Business logic, domain models, and repository interfaces.
- `:core:data`: Data layer implementation, including local storage and repository implementations.
- `:core:network`: Remote data source implementation using Retrofit.
- `:core:ui`: Shared UI components and resources.

## Architecture

This project follows **Clean Architecture** principles:

1.  **Domain Layer**: Contains high-level business rules (UseCases) and data models. It is independent of any other layer.
2.  **Data Layer**: Responsible for data retrieval from local or remote sources. It implements the interfaces defined in the Domain layer.
3.  **UI Layer**: Uses the MVI pattern. ViewModels expose a single `UiState` flow and handle `Events` from the UI.


## AI Usage Disclosure

This project was developed with selective AI assistance. AI was mainly used to speed up repetitive tasks, explore implementation alternatives, and refine documentation, while the system design, implementation trade-offs, and final code decisions were made by me.

- **Architecture & Design Discussion**: AI was used as a discussion partner to validate architectural ideas, implementation approaches, and design trade-offs.
- **Boilerplate Support**: AI helped speed up repetitive code generation, such as API response models.
- **UI / Compose Suggestions**: Some UI details and layout ideas were refined with AI assistance, while most Compose implementation was written manually.
- **Testing Support**: AI was used to assist with parts of the testing process, such as brainstorming test scenarios and refining test code structure.
- **Documentation**: AI was used to improve the clarity and wording of the README and selected code comments.
- **Core Implementation**: The business logic, use cases, repository behavior, dependency injection setup, and overall integration were implemented and reviewed manually.

## License

This project is licensed under the MIT License.
