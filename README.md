# 🚀 Full-Stack Android eCommerce Solution – Buyer & Seller Apps  
*(This is the Seller version)*  
**Buyer Version** 👉 [ShopEase_frBuyer](https://github.com/101paul/ShopEase_frBuyer)

An end-to-end **native Android eCommerce ecosystem**, designed to deliver a seamless, real-time, and secure experience for both **buyers** and **sellers**.

---

## 📱 Two Apps – One Powerful Ecosystem

<div align="center">
  <img src="images/logo.png" alt="Logo" width="200" style="display: block; margin: auto;" />
</div>

This project includes **two fully functional Android applications**, built from the ground up:

- 🛒 **Buyer App** – For browsing products, managing cart, placing orders, making secure payments, and tracking delivery  
- 🛍️ **Seller App** – For uploading products, managing stock, and processing incoming orders  

---

## 🛠️ Tech Stack & Architecture

<div align="center">
  <img src="images/seller1.png" width="200" style="display: inline-block; margin:10px;"/>
  <img src="images/seller2.png" width="200" style="display: inline-block; margin:10px;"/>
  <img src="images/seller3.png" width="200" style="display: inline-block; margin:10px;"/>
</div>

- **Kotlin + XML** – Modern, fast, and intuitive native UI development  
- **MVVM Architecture** – Clean code separation and lifecycle-aware components  
- **Room Database** – Efficient offline access and persistent cart state  
- **SharedPreferences** – Lightweight local state management  
- **Firebase Realtime Database** – Instant syncing of orders, stock, and product data  
- **Firebase Cloud Storage** – Fast and scalable image uploads & retrieval  

---

## 🔐 Authentication & Security

<div align="center">
  <img src="images/sellerOtp1.png" width="200" style="display: inline-block; margin:10px;"/>
  <img src="images/sellerOtp2.png" width="200" style="display: inline-block; margin:10px;"/>
</div>

- **OTP Login via Firebase Authentication**
  - Secure, passwordless access  
  - Fast mobile number verification  
  - Seamless onboarding for both buyers and sellers  

---

## 🛍️ Product Management & Editing

<div align="center">
  <img src="images/editProduct1.png" width="200" style="display: inline-block; margin:10px;"/>
  <img src="images/editProduct2.png" width="200" style="display: inline-block; margin:10px;"/>
</div>

- Upload product with image, price, and stock  
- **Edit product** title, description, price, and image  
- Delete or update items from your live catalog  

---

## 🔍 Search & Location

<div align="center">
  <img src="images/search1.png" width="200" style="display: inline-block; margin:10px;"/>
  <img src="images/address1.png" width="200" style="display: inline-block; margin:10px;"/>
</div>

- Smart **search** bar to find product instantly  
- Store **business address** in seller settings  
- Use location for delivery reference  

---

## 💳 Payment Gateway Integration

- **Razorpay Payment Gateway**
  - Works with Buyer App to initiate secure payments  
  - Seller gets notified of payment status and order confirmation

- **Custom Java + Spring Boot Backend**
  - Validates and verifies payment transactions  
  - Protects sensitive data and ensures order integrity  

---
## 🛍️ Product Management

#➕ Add Product
<div align="center"> <img src="images/addProduct1.png" width="200" style="display: inline-block; margin:10px;" /> <img src="images/addProduct2.png" width="200" style="display: inline-block; margin:10px;" /> </div>
Add a new product with title, description, price, stock, and category

Upload up to 5 images with preview support

Automatically updates in Firebase and visible instantly in buyer version

Realtime validation for missing or incorrect data

#✏️ Edit Existing Product
<div align="center"> <img src="images/editProduct1.png" width="200" style="display: inline-block; margin:10px;" /> <img src="images/editProduct2.png" width="200" style="display: inline-block; margin:10px;" /> </div>
Tap on any existing product to edit its details

Modify title, description, price, stock, unit, or replace images

Realtime updates reflect immediately for the buyer

Smooth, custom dialog with pre-filled current values


## ⚡ Real-Time Order Sync

<div align="center">
  <img src="images/orderUpdate1.png" width="200" style="display: inline-block; margin:10px;"/>
  <img src="images/orderUpdate2.png" width="200" style="display: inline-block; margin:10px;"/>
</div>

- Get **live notifications** when a buyer places an order  
- Mark orders as **packed, shipped, delivered**  
- View buyer contact & address details  

---

## ✅ Why This App Stands Out

- 🔥 **Native Android development** – Fast & responsive  
- 📐 **MVVM architecture** – Organized and scalable  
- 🛠️ **Full-stack integration** – From UI to payment  
- 🔐 **Secure & real-time** – Built using Firebase & Spring Boot  
- 🚀 **Ready for production** – Smooth UX for both parties  
- 🌐 **Cloud-powered image and data storage**  
- 💼 **Perfect for local businesses & small sellers**  

---

## 📦 Features At A Glance

| Feature                            | Buyer App ✅ | Seller App ✅ |
|-----------------------------------|--------------|----------------|
| OTP Login                         | ✅           | ✅             |
| Realtime Cart                     | ✅           |                |
| Product Upload & Management       |              | ✅             |
| Product Editing                   |              | ✅             |
| Stock Management                  |              | ✅             |
| Live Order Updates                | ✅           | ✅             |
| Razorpay Payment Gateway          | ✅           |                |
| Order Verification via Backend    | ✅           | ✅             |
| Firebase Sync (Realtime + Images) | ✅           | ✅             |
| Offline Cart Storage (Room)       | ✅           |                |
| 🔍 Product Search (Seller App)     |              | ✅             |
| 📍 Address Management              |              | ✅             |

---


