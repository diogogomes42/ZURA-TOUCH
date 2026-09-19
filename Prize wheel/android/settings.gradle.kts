pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ZuraTouchPrizeWheel"
include(":app-customer")
include(":app-operator")
include(":feature:sale-wheel")
include(":feature:sale-cards")
include(":feature:direct-purchase")
include(":core:domain")
include(":core:data")
include(":core:hardware")
include(":core:payment")
include(":core:vending")
include(":core:feedback")
