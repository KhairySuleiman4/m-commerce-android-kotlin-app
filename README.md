
# 🛍️ Pick’n Pay

**Pick’n Pay** is a modern M-Commerce Android application built using Kotlin and Jetpack Compose. It enables users to browse, search, filter, and purchase products online through a clean, mobile-first experience. The app integrates Shopify Storefront API and Firebase Authentication to ensure secure and seamless shopping.

---

## 📱 Features

- 🔐 **User Authentication** — Secure registration and login with Firebase.
- 🛒 **Product Browsing** — Discover products by categories, brands, bestsellers, and latest arrivals.
- 🔎 **Search & Filter** — Search by name and filter by brand, category, and price.
- ❤️ **Wishlist** — Save favorite products for later.
- 🛍️ **Shopping Cart** — Add, remove, and manage items before checkout.
- 📦 **Order History** — Review past orders.
- 💳 **Checkout & Payments** — Simulated checkout process with confirmation emails.
- 🌍 **Currency Switching** — Convert prices using ExchangeRate-API.

---

## 🧱 Architecture

The project follows **Clean Architecture** and **MVI (Model-View-Intent)** for scalable and maintainable code.

```
Presentation (UI)
 └── Jetpack Compose, ViewModel (State, Intents)
Domain (Business Logic)
 └── UseCases, Models, Interfaces
Data (Data Sources)
 └── Shopify API, Firebase, Repositories, Mappers
```

---

## 🔧 Technologies Used

### Android Stack
- Kotlin
- Jetpack Compose
- Coroutines
- Hilt (Dependency Injection)

### Backend & APIs
- Shopify Storefront API
- Apollo GraphQL Client
- Firebase Authentication
- Firebase Firestore
- ExchangeRate-API
- Shopify Checkout Sheet Kit
- DataStore (Preferences)

### Testing
- JUnit
- MockK

### Tools
- GitHub (Version Control)
- Postman (API Testing)
- Trello (Project Management)

---

## 📷 Screenshots
![splash](https://github.com/user-attachments/assets/ec42a4a9-f42a-4e36-b071-faa053e2c508)
![signup](https://github.com/user-attachments/assets/a1eb18ff-d003-4d7a-90fc-bd6570e917fe)
![login](https://github.com/user-attachments/assets/56799fb0-f20d-4b05-85c9-2765a4a68993)
![home](https://github.com/user-attachments/assets/25e87f6e-6d0f-470b-9359-dd2452cd0a22)
![search](https://github.com/user-attachments/assets/045dbc8a-5304-48b0-90bb-d6ada8282bb7)
![categories](https://github.com/user-attachments/assets/885119ca-ebb5-4896-8def-72e496b5268c)
![categories_data](https://github.com/user-attachments/assets/fa7cea52-d0ea-4dfe-9e8a-9d1f5b57802c)
![product-info](https://github.com/user-attachments/assets/29a629a7-b043-44be-a42c-34dc513712d3)
![cart](https://github.com/user-attachments/assets/bb3081e6-1521-4d96-9220-4715344f96dd)
![profile_user](https://github.com/user-attachments/assets/7e7a2a5a-c286-4f9e-b56b-fb6676bcc1cf)
![orders](https://github.com/user-attachments/assets/efee4851-390b-4136-b5a2-0002ce079269)
![order-details](https://github.com/user-attachments/assets/7dbec49e-419b-45b1-8f48-120f4d06c417)
![about](https://github.com/user-attachments/assets/b5c6e75e-38cb-4c47-b4c6-51777f2738e8)

---

## 📁 Project Structure

```
app/
├── presentation/     # UI (Jetpack Compose + ViewModels)
├── domain/           # Business logic (UseCases, Models)
├── data/             # Repositories, Network, Mappers
├── di/               # Hilt modules
├── utils/            # Helpers & constants
```

---

## 🧪 Testing

We used unit tests to ensure the correctness of the app using:

- JUnit for test cases
- MockK for mocking dependencies

---

## 🚧 Challenges Faced

- Adopting **MVI** and **Clean Architecture** for the first time.
- Synchronizing **Firebase** users with **Shopify** customers.
- Integrating **Shopify Admin** and **Storefront APIs** for order creation.
- Sending verification emails via Firebase.
- Managing real-time updates and secure payment flow.

---

## 👨‍💻 Authors

- [Hazem Mahmoud](https://github.com/Hazem-web)
- [Khairy Hatem](https://github.com/KhairySuleiman4)
- [Shereen Mohamed](https://github.com/shereenmohamed923)

Supervised by [**Eng. Yasmeen Hosny**](https://github.com/yasmeenhosny98)

---

## 🔗 References

- [Shopify Storefront API](https://shopify.dev/docs/api/storefront)
- [Kotlin Docs](https://kotlinlang.org/docs/home.html)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Firebase Authentication](https://firebase.google.com/docs/auth)
- [Apollo GraphQL Client](https://www.apollographql.com/docs/kotlin)
- [ExchangeRate-API](https://www.exchangerate-api.com/docs/overview)
