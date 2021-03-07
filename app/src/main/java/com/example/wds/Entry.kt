package com.example.wds

@Suppress("JoinDeclarationAndAssignment")
class Entry {
    private lateinit var imgSrc: String
    private lateinit var textAnimal: String
    private lateinit var textTime: String
    constructor() {}
    constructor(imgSrc: String, textAnimal: String, textTime: String) {
        this.imgSrc = imgSrc
        this.textAnimal = textAnimal
        this.textTime = textTime
    }
    fun getImgSrc() = imgSrc
    fun getTextAnimal() = textAnimal
    fun getTextTime() = textTime
}