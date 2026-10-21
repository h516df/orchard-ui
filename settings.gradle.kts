pluginManagement { repositories { google(); mavenCentral(); gradlePluginPortal() } }
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories { google(); mavenCentral() }
}
rootProject.name = "OrchardUI"
include(":orchard", ":catalog")

project(":orchard").projectDir = file("adapters/compose/orchard")
project(":catalog").projectDir = file("examples/compose-catalog")
