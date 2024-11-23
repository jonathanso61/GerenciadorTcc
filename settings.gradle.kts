pluginManagement {
    repositories {
        google()  // Repositório do Google
        mavenCentral()  // Repositório Maven Central
        gradlePluginPortal()  // Repositório de plugins do Gradle
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)  // Impede repositórios no módulo
    repositories {
        google()  // Repositório do Google
        mavenCentral()  // Repositório Maven Central
    }
}

rootProject.name = "Gerenciador de Tarefas"
include(":app")
