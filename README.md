🛒 Smart Shopping Cart Using YOLOv6 & Real-Time Object Detection

Modern shopping should be fast, easy, and contactless. This project presents an AI-powered Smart Shopping Cart system that replaces manual barcode scanning with **real-time product detection** using deep learning. By using a smartphone camera and a trained YOLOv6 model, the system can detect items instantly and retrieve price and expiry details from a local database, streamlining the entire retail experience.



🎯 Project Objective

To develop a mobile-based smart shopping system that:
- Detects retail products in real time using a trained YOLOv6 model.
- Displays product details like name, price, and expiry directly in the app.
- Allows users to manage their cart and checkout without barcodes or extra hardware.



🧠 Core Technologies

- YOLOv6 for real-time object detection
- Roboflow for dataset creation and augmentation
- ONNX for model conversion (YOLOv6 → mobile compatibility)
- Android Studio with Java & Kotlin for mobile app development
- SQLite for product info storage and retrieval



🏗️ System Architecture

### 📷 Real-Time Detection Workflow:
1. User opens the app and starts scanning items.
2. The app uses the phone camera + YOLOv6 model to detect products.
3. Detected labels are matched to a local **SQLite** database.
4. Product name, price, and expiry are displayed.
5. User can add/remove items from cart and proceed to checkout.



🧪 Model Training Overview

- Dataset built with product images manually annotated in **Roboflow**
- Images resized and augmented (flip, brightness, rotate)
- Trained in **Google Colab** using GPU
- Achieved:
  - `mAP@0.50: 98.3%`
  - `mAP@0.50:0.95: 71.6%`
  - Strong F1-scores across classes
- Small object detection remains a challenge; future training will improve that



📱 App Features

- **Login Page** (with future scope for secure auth)
- **Live Object Detection** via mobile camera
- **Cart Management** (add/remove/view items)
- **Product Info** (name, price, expiry, calories, etc.)
- **Offline database** powered by SQLite



🧩 System Modules

| Module                 | Description                                               |
|------------------------|-----------------------------------------------------------|
| Dataset Generator      | Built via Roboflow with image augmentation                |
| YOLOv6 Model           | Trained in Colab, converted to ONNX for mobile use        |
| Android App            | Built in Android Studio using Java/Kotlin                |
| Database (SQLite)      | Stores product details for local lookup                   |
| Cart & Checkout        | Manages items and computes total                          |



📊 Result Highlights

| Metric                        | Value   |
|-------------------------------|---------|
| `mAP@[IoU=0.50]`              | 98.3%   |
| `mAP@[IoU=0.50:0.95]`         | 71.6%   |
| `Average Recall (medium)`     | 75.8%   |
| `Average Recall (large)`      | 77.1%   |
| `F1 Scores`                   | > 0.7-0.9 across all classes |








