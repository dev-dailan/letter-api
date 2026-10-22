plugins {
    kotlin("jvm") version "2.3.21"
    kotlin("plugin.spring") version "2.3.21"
    id("org.springframework.boot") version "4.1.1"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "2.3.21"
    // Spring Boot 가 관리하는 jOOQ 버전과 맞춘다.
    id("org.jooq.jooq-codegen-gradle") version "3.21.7"
}

group = "com.alsora"
version = "0.0.1-SNAPSHOT"
description = "letter-api"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.springframework.boot:spring-boot-starter-liquibase")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.module:jackson-module-kotlin")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    jooqCodegen("org.jooq:jooq-meta-extensions:3.21.7")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

// jOOQ 파서가 읽지 못하는 PostgreSQL 전용 구문(표현식 인덱스 등)이 있어
// 코드 생성용으로만 인덱스 구문을 걸러낸 사본을 만든다. (원본을 고치면 Liquibase 체크섬이 바뀐다)
// 인덱스는 한 줄로 작성한다는 전제다.
val jooqDdl = tasks.register<Sync>("jooqDdl") {
    from("src/main/resources/db/changelog/changes") {
        include("*.sql")
        filter { line -> if (Regex("^\\s*CREATE\\s+(UNIQUE\\s+)?INDEX\\b", RegexOption.IGNORE_CASE).containsMatchIn(line)) "" else line }
    }
    into(layout.buildDirectory.dir("jooq-ddl"))
}

// Liquibase 마이그레이션 SQL 을 파싱해서 jOOQ 코드를 생성한다. (DB / Docker 불필요)
jooq {
    configuration {
        generator {
            name = "org.jooq.codegen.KotlinGenerator"
            database {
                name = "org.jooq.meta.extensions.ddl.DDLDatabase"
                properties {
                    property {
                        key = "scripts"
                        value = "build/jooq-ddl/*.sql"
                    }
                    property {
                        key = "sort"
                        value = "semantic"
                    }
                    property {
                        key = "defaultNameCase"
                        value = "lower"
                    }
                    // 파서가 모르는 PostgreSQL 전용 구문은 건너뛴다. (코드 생성에는 테이블/컬럼만 필요)
                    property {
                        key = "parseUnsupportedSyntax"
                        value = "IGNORE"
                    }
                }
                excludes = "databasechangelog.*"
            }
            generate {
                isPojos = false
                isDaos = false
                isRecords = true
                isFluentSetters = false
                isJavaTimeTypes = true
            }
            target {
                packageName = "com.alsora.letter.jooq"
                directory = "build/generated-sources/jooq"
            }
        }
    }
}

tasks.named("jooqCodegen") {
    dependsOn(jooqDdl)
    inputs.dir(jooqDdl.map { it.destinationDir })
}

sourceSets.main {
    kotlin.srcDir(tasks.named("jooqCodegen"))
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.bootJar {
    archiveFileName = "letter-api.jar"
}

tasks.jar {
    enabled = false
}

// AWS Lambda(zip + Lambda Web Adapter 레이어) 배포 패키지: build/distributions/letter-api-lambda.zip
val lambdaZip by tasks.registering(Zip::class) {
    group = "build"
    description = "Builds the AWS Lambda deployment package."
    archiveFileName = "letter-api-lambda.zip"
    destinationDirectory = layout.buildDirectory.dir("distributions")
    from(tasks.bootJar.map { zipTree(it.archiveFile) })
    from("src/lambda/run.sh") {
        filePermissions { unix("rwxr-xr-x") }
    }
}
