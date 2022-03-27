package io.frankmayer.naruto

import io.frankmayer.naruto.Jutsu.*

enum class EJutsu (val jutsu: IJutsu) {
    RASENGAN(Rasengan()),
    SHINRATENSEI(ShinraTensei()),
    AMENOTEJIKARA(Amenotejikara()),
    KIRIN(Kirin()),
    HIRAISHINNOJUTSU(HiraishinNoJutsu()),
    GOKAKYUNOJUTSU(GokakyuNoJutsu()),
    SUIROUNOJUTSU(SuirouNoJutsu()),
    SUIJINHEKI(Suijinheki())
}
