package flashcards

import com.squareup.moshi.Moshi // implementation("com.squareup.moshi:moshi-kotlin:1.11.0")
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File
import kotlin.random.Random

class Card(private val term: String, private val definition: String) {
    private var mistakes = 0

    fun getTerm(): String = this.term
    fun getDefinition(): String = this.definition
    fun getMistakes() = mistakes
    fun plusMistake() = mistakes++
    fun eraseMistakes() { mistakes = 0 }
    @Override
    override fun toString(): String {
        return "term: $term, definition: $definition"
    }
}

class FlashCards {

    private val flashCards = mutableListOf<Card>()

    private val log = mutableListOf<String>()

    fun getFlashCardsList() = flashCards

    fun getCardByIndex(index: Int): Card? {
        return if (index < flashCards.size) flashCards[index] else null
    }

    fun getLog() = log

    fun addLog(line: String) {
        log.add(line)
    }

    fun printLog() {
        for (line in log) {
            println("LOG: $line")
        }
    }

    fun displayFlashCards() {
        if (flashCards.isEmpty()) {
            println("No cards")
        } else {
            for (card in flashCards) {
                println(card)
            }
        }
    }

    fun addCard(card: Card) {
        flashCards.add(card)
    }

    fun removeCard(index: Int) {
        flashCards.removeAt(index)
    }

    fun replaceCard(index: Int, card: Card) {
        flashCards[index] = card
    }

    fun cardPresent(card: Card): Int? {
        var index: Int? = null
        for (c in flashCards.indices) {
            if (card.getTerm() == flashCards[c].getTerm()) {
                index = c
            }
        }
        return index
    }
}

/**
 * Adds new card with term and definition to FlashCards
 */
fun add(flashCards: FlashCards) {
    val termMessage = "The card:"
    println(termMessage)
    flashCards.addLog(termMessage)
    // checking for term occurrences
    val userTerm = readln()
    flashCards.addLog(userTerm)
    for (card in flashCards.getFlashCardsList()) {
        if (userTerm.lowercase() == card.getTerm().lowercase()) {
            val existsTermMessage = "The card \"$userTerm\" already exists.\n"
            println(existsTermMessage)
            flashCards.addLog(existsTermMessage)
            return
        }
    }
    // checking for definition occurrences
    val defMessage = "The definition of the card:"
    println(defMessage)
    flashCards.addLog(defMessage)
    val userDef = readln()
    flashCards.addLog(userDef)
    for (card in flashCards.getFlashCardsList()) {
        if (userDef.lowercase() == card.getDefinition().lowercase()) {
            val existsDefMessage = "The definition \"$userDef\" already exists.\n"
            println(existsDefMessage)
            flashCards.addLog(existsDefMessage)
            return
        }
    }
    // adding new card
    val card = Card(userTerm,userDef)
    flashCards.addCard(card)
    val addedMessage = "The pair (\"$userTerm\":\"$userDef\") has been added.\n"
    println(addedMessage)
    flashCards.addLog(addedMessage)
}

/**
 * Removes a card from FlashCards by its term
 */
fun remove(flashCards: FlashCards) {
    val cardMessage = "Which card?"
    println(cardMessage)
    flashCards.addLog(cardMessage)
    val userInput = readln()
    flashCards.addLog(userInput)
    for (c in flashCards.getFlashCardsList().indices) {
        if (userInput.lowercase() == flashCards.getFlashCardsList()[c].getTerm().lowercase()) {
            flashCards.removeCard(c)
            val removedMessage = "The card has been removed.\n"
            println(removedMessage)
            flashCards.addLog(removedMessage)
            return
        }
    }
    val noCardMessage = "Can't remove \"$userInput\": there is no such card.\n"
    println(noCardMessage)
    flashCards.addLog(noCardMessage)
}

/**
 * Imports FlashCards from json format file with high priority values
 */
fun import(flashCards: FlashCards) {
    val fileMessage = "File name:"
    println(fileMessage)
    flashCards.addLog(fileMessage)
    val userInput = readln()
    flashCards.addLog(userInput)
    val file = File(userInput)
    if (file.exists()) {
        // standard builder pattern
        val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        // creating new parametrized type to write list to object
        val type = Types.newParameterizedType(MutableList::class.java, Card::class.java)
        val flashCardsAdapter = moshi.adapter<MutableList<Card>>(type)
        // reading .json file and creating new list of objects
        val impFlashCards = flashCardsAdapter.fromJson(file.readText())
        // adding imported cards to existing with priority of imported ones
        if (impFlashCards != null) {
            for (impCard in impFlashCards) {
                val indexOfCardToReplace = flashCards.cardPresent(impCard)
                if (indexOfCardToReplace != null) {
                    flashCards.replaceCard(indexOfCardToReplace, impCard)
                } else {
                    flashCards.addCard(impCard)
                }
            }
        }
        val loadedMessage = "${impFlashCards?.size} cards have been loaded.\n"
        println(loadedMessage)
        flashCards.addLog(loadedMessage)
    } else {
        val noFileMessage = "File not found.\n"
        println(noFileMessage)
        flashCards.addLog(noFileMessage)
    }
}

/**
 * Imports FlashCards from json format file at the beginning if app arguments say so
 */
fun importArgs(flashCards: FlashCards, import: String) {
    val file = File(import)
    if (file.exists()) {
        // standard builder pattern
        val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        // creating new parametrized type to write list to object
        val type = Types.newParameterizedType(MutableList::class.java, Card::class.java)
        val flashCardsAdapter = moshi.adapter<MutableList<Card>>(type)
        // reading .json file and creating new list of objects
        val impFlashCards = flashCardsAdapter.fromJson(file.readText())
        // adding imported cards to existing with priority of imported ones
        if (impFlashCards != null) {
            for (impCard in impFlashCards) {
                val indexOfCardToReplace = flashCards.cardPresent(impCard)
                if (indexOfCardToReplace != null) {
                    flashCards.replaceCard(indexOfCardToReplace, impCard)
                } else {
                    flashCards.addCard(impCard)
                }
            }
        }
        val loadedMessage = "${impFlashCards?.size} cards have been loaded.\n"
        println(loadedMessage)
        flashCards.addLog(loadedMessage)
    } else {
        val noFileMessage = "File not found.\n"
        println(noFileMessage)
        flashCards.addLog(noFileMessage)
    }
}

/**
 * Exports FlashCards to json format file
 */
fun export(flashCards: FlashCards) {
    val fileMessage = "File name:"
    println(fileMessage)
    flashCards.addLog(fileMessage)
    val userInput = readln()
    flashCards.addLog(userInput)
    val jsonFile = File(userInput)
    // standard builder pattern
    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    // creating new parametrized type to extract list from object
    val type = Types.newParameterizedType(MutableList::class.java, Card::class.java)
    val flashCardsAdapter = moshi.adapter<MutableList<Card>>(type)
    // addressing the list with adapter and new type
    jsonFile.writeText(flashCardsAdapter.indent(" ").toJson(flashCards.getFlashCardsList()))
    val savedMessage = "${flashCards.getFlashCardsList().size} cards have been saved.\n"
    println(savedMessage)
    flashCards.addLog(savedMessage)
}

/**
 * Exports FlashCards to json format file at the end if app arguments say so
 */
fun exportArgs(flashCards: FlashCards, export: String) {
    val jsonFile = File(export)
    // standard builder pattern
    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    // creating new parametrized type to extract list from object
    val type = Types.newParameterizedType(MutableList::class.java, Card::class.java)
    val flashCardsAdapter = moshi.adapter<MutableList<Card>>(type)
    // addressing the list with adapter and new type
    jsonFile.writeText(flashCardsAdapter.indent(" ").toJson(flashCards.getFlashCardsList()))
    val savedMessage = "${flashCards.getFlashCardsList().size} cards have been saved.\n"
    println(savedMessage)
    flashCards.addLog(savedMessage)
}

/**
 * Asks user to name presented card's definition
 */
fun ask(flashCards: FlashCards) {
    val timesMessage = "How many times to ask?"
    println(timesMessage)
    flashCards.addLog(timesMessage)
    val userInput = readln()
    flashCards.addLog(userInput)
    if (userInput.toIntOrNull() != null && userInput.toInt() > 0) {
        var cardsToAsk = userInput.toInt()
        if (flashCards.getFlashCardsList().isNotEmpty()) {
            while (cardsToAsk != 0) {
                val randomCardIndex = Random.nextInt(0, flashCards.getFlashCardsList().size)
                val card = flashCards.getCardByIndex(randomCardIndex)
                val defMessage = "Print the definition of \"${card?.getTerm()}\":"
                println(defMessage)
                flashCards.addLog(defMessage)
                val userAnswer = readln()
                flashCards.addLog(userAnswer)
                // checking for correct answer
                if (userAnswer.lowercase() == card?.getDefinition()?.lowercase()) {
                    val correctMessage = "Correct!"
                    println(correctMessage)
                    flashCards.addLog(correctMessage)
                } else {
                    card?.plusMistake()
                    var wrongMessage = "Wrong. The right answer is \"${card?.getDefinition()}\""
                    // looking for the correct answer through other cards
                    for (c in flashCards.getFlashCardsList()) {
                        if (userAnswer.lowercase() == c.getDefinition().lowercase()) {
                            wrongMessage += ", but your definition is correct for \"${c.getTerm()}\" card"
                        }
                    }
                    wrongMessage += "."
                    println(wrongMessage)
                    flashCards.addLog(wrongMessage)
                }
                cardsToAsk--
            }
            println()
        } else {
            val noCardsMessage = "There are no cards.\n"
            println(noCardsMessage)
            flashCards.addLog(noCardsMessage)
        }
    } else {
        val wrongMessage = "Wrong input.\n"
        println(wrongMessage)
        flashCards.addLog(wrongMessage)
    }
}

/**
 * Saves all input/output lines to text file
 */
fun log(flashCards: FlashCards) {
    try {
        val fileMessage = "File name:"
        println(fileMessage)
        flashCards.addLog(fileMessage)
        val userInput = readln()
        flashCards.addLog(userInput)
        val file = File(userInput)
        for (line in flashCards.getLog()) {
            file.appendText("$line\n")
        }
        println("The log has been saved.\n")
    } catch (e:Exception) {
        val wrongMessage = "Incorrect file name.\n"
        println(wrongMessage)
        flashCards.addLog(wrongMessage)
    }
}

/**
 * Shows which cards have the highest number of mistakes
 */
fun hardestCard(flashCards: FlashCards) {
    // defining the card with the highest number of mistakes
    val maxMistakesCard = flashCards.getFlashCardsList().maxByOrNull { it.getMistakes() }
    // filtering all cards with the highest number of mistakes in a separate list
    val maxMistakesList = flashCards.getFlashCardsList().filter { it.getMistakes() != 0 && it.getMistakes() == maxMistakesCard?.getMistakes() }
    if (maxMistakesList.size == 1) {
        val singleMessage = "The hardest card is \"${maxMistakesList[0].getTerm()}\". " +
                "You have ${maxMistakesList[0].getMistakes()} errors answering it.\n"
        println(singleMessage)
        flashCards.addLog(singleMessage)
    } else if (maxMistakesList.isEmpty()) {
        val noMistMessage = "There are no cards with errors.\n"
        println(noMistMessage)
        flashCards.addLog(noMistMessage)
    } else {
        // creating a string with multiple terms in ""
        val maxMistakesTerms = (maxMistakesList.map { "\"" + it.getTerm() + "\"" }).joinToString(", ")
        val multiMessage = "The hardest cards are $maxMistakesTerms. " +
                "You have ${maxMistakesList[0].getMistakes()} errors answering them.\n"
        println(multiMessage)
        flashCards.addLog(multiMessage)
    }
}

/**
 * Resets mistakes values for sll cards back to 0
 */
fun resetStats(flashCards: FlashCards) {
    for (card in flashCards.getFlashCardsList()) {
        card.eraseMistakes()
    }
    val resetMessage = "Card statistics have been reset.\n"
    println(resetMessage)
    flashCards.addLog(resetMessage)
}

/**
 * Menu for working with flash cards
 */
fun flashCards(import: String?, export: String?) {
    val flashCards = FlashCards()
    // importing cards from file if app arguments say so
    if (import != null) {
        importArgs(flashCards, import)
    }
    // main app menu
    do {
        val menu = "Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):"
        println(menu)
        flashCards.addLog(menu)
        val userInput = readln().lowercase()
        flashCards.addLog(userInput)
        when (userInput) {
            "add" -> add(flashCards)
            "remove" -> remove(flashCards)
            "import" -> import(flashCards)
            "export" -> export(flashCards)
            "ask" -> ask(flashCards)
            "exit" -> println("Bye bye!")
            "log" -> log(flashCards)
            "hardest card" -> hardestCard(flashCards)
            "reset stats" -> resetStats(flashCards)
            else -> {
                val wrongMessage = "Wrong input!\n"
                println(wrongMessage)
                flashCards.addLog(wrongMessage)
            }
        }
    } while (userInput != "exit")
    // exporting cards to file if app arguments say so
    if (export != null) {
        exportArgs(flashCards, export)
    }
}

fun main(args: Array<String>) {
    var import: String? = null
    var export: String? = null
    if (args.contains("-import")) {
        val importIndex = args.indexOf("-import") + 1
        if (args.size > importIndex) {
            import = args[importIndex]
        }
    }
    if (args.contains("-export")) {
        val exportIndex = args.indexOf("-export") + 1
        if (args.size > exportIndex) {
            export = args[exportIndex]
        }
    }
    flashCards(import, export)
}