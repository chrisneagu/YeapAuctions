# RBW Auctions

An android app for online auctions. Started as a project designed for my bachelor's thesis, originally named YeapAuctions, got a new name of RBW Auctions (*Register*, *Bid*, *Win*)

# Objective

People over 18 years can register into the app with otp sms verification and can search and place bids over lots of products grouped as auctions.

# Description

The application allows creating an account through which the user can find lots of products tht satisfy theyr requirements, placing bids and winning like the fictive company name shows.

# Technologies

*Kotlin*

*Jetpack Compose*

*Node.js*

*MongoDB*

*Ktor framework* - makes the link between the kotlin http client and the express http server

*Socket.io* - making events and broadcasting between server and clients

# APIs

*Twilio* - OTP SMS Verification

*SendGrid* - sending email notifications

*Stripe* - payments using intent

# Screens

*Home Screen*

- If in search module return a list of lots based on the query, filtered by information field in the description collection, lot name, lot category and lot subcategory
- If not in search module return a list of auctions
- Filter auctions by country and by the fact that it is closed or not

![Screenshot_20230611_155708](https://github.com/chrisneagu/YeapAuctions/assets/57600322/6d7f5b09-44a8-4d82-800c-231e86863aa1)

*Categories Screen*

- Static categories list view made by a local categories data provider
- Query lots by category and show them in a secondary screen

![Screenshot_20230611_155745](https://github.com/chrisneagu/YeapAuctions/assets/57600322/fdf1bbd3-256d-4ab3-a8a3-dc1b4c697de2)

*My lots*

- Favorites screen that acts as a single page and queries the lots based current, won and archive. Had to make an additional collection named Link.

![image](https://github.com/chrisneagu/YeapAuctions/assets/57600322/477c6573-9498-46a6-a48b-1d86d767f67b)

*My RBW*

- Shows a welcome message
- *My lots* row redirects user to *My lots* screen
- *My payments* opens a secondary screen and shows payment orders and can do operations like generating payments/ invoice pdf
- *My details* opens a secondary screen and shows user information that can be editable
- Logout button logs the user out and redirects to login page

![image](https://github.com/chrisneagu/YeapAuctions/assets/57600322/1f2e85e5-1c33-47f7-93e1-62e893666e0f)

