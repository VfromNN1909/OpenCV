package me.vlasoff.kotlinopencv

enum class PassportImageType(val page: String) {
    MAIN_PAGE("main"),
    REGISTRATION_PAGE("registration"),
    SELFIE("selfie"),
    NONE("none")
}