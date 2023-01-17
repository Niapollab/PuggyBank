package ru.vsu.puggybank.gazprom

import ru.vsu.puggybank.dto.gazprom.TechInfo

data class GazpromCardsResponse (val result: Int, val techInfo: TechInfo, val cards: Array<Card>)