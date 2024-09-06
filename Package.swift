// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "SendIntent",
    platforms: [.iOS(.v13)],
    products: [
        .library(
            name: "SendIntent",
            targets: ["SendIntentPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", branch: "main")
    ],
    targets: [
        .target(
            name: "SendIntentPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/SendIntentPlugin"),
        .testTarget(
            name: "SendIntentPluginTests",
            dependencies: ["SendIntentPlugin"],
            path: "ios/Tests/SendIntentPluginTests")
    ]
)