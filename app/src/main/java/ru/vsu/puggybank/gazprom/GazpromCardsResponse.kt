package ru.vsu.puggybank.gazprom

import ru.vsu.puggybank.dto.gazprom.TechInfo

@kotlinx.serialization.Serializable
data class GazpromCardsResponse (val result: Int, val techInfo: TechInfo, val cards: Array<Card>)