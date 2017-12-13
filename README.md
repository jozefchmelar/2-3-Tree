# 2-3-Tree

2-3 Tree implementation in Kotlin without use of recursion as a part of Algorithms and Datastrucures 2 @ Faculty of Management Science and Informatics, University in Žilina, Slovakia

With a sample program that was used as a demonstration which had to do following  (in slovak) :
 
Pre pripravovaný projekt elektronickej zdravotnej karty malého štátu navrhnite a
implementujte demonštračný systém. Tento systém bude umožňovať použitie základných
administratívnych úkonov, ktoré budú neskôr poskytnuté vo finálnej verzii informačného systému
zdravotnej dokumentácie obyvateľstva. Celá databáza bude centralizovaná (lokálne počítače tam
budú realizovať svoje požiadavky) a bude sa nachádzať v operačnej pamäti, aby sa zabezpečila
maximálna rýchlosť spracovania požiadaviek. V demonštračnej verzii sa budú evidovať pacienti,
ich hospitalizácie, lieky a ich vyšetrenia. V demonštračnej verzii nie je potrebné zabezpečiť
vzdialený prístup k centrálnej databáze, ale je potrebné umožniť výpis všetkých evidovaných údajov,
tak aby bolo možné skontrolovať funkčnosť programu.
Pre každého pacienta evidujte nasledovné údaje:
 krstné meno
 priezvisko
 rodné číslo (reťazec)
 dátum narodenia
 kód zdravotnej poisťovne
 záznamy o všetkých jeho hospitalizáciách
Pre každú hospitalizáciu pacienta evidujte:
 dátum začiatku hospitalizácie
 dátum konca hospitalizácie
 diagnózu s ktorou bol prijatý
Pre každú nemocnicu evidujte:
 názov nemocnice
 záznamy o všetkých hospitalizáciách
Informačný systém musí umožňovať tieto základné operácie (operácie sú zoradené podľa
početnosti ich využívania):
1. vyhľadanie záznamov pacienta (identifikovaný svojím rodným číslom) v zadanej
nemocnici (identifikovaná svojím názvom). Po nájdení pacienta je potrebné zobraziť
všetky evidované údaje.
2. vyhľadanie záznamov pacienta/ov v zadanej nemocnici (identifikovaná svojím názvom)
podľa mena a priezviska. Po nájdení pacienta/ov je potrebné zobraziť všetky evidované
údaje.
3. vykonanie záznamu o začiatku hospitalizácie pacienta (identifikovaný svojím rodným
číslom) v nemocnici (identifikovaná svojím názvom)
4. vykonanie záznamu o ukončení hospitalizácie pacienta (identifikovaný svojím rodným
číslom) v nemocnici (identifikovaná svojím názvom)
5. výpis hospitalizovaných pacientov v nemocnici (identifikovaná svojím názvom) v
zadanom časovom období (od, do)
6. pridanie pacienta
7. vytvorenie podkladov pre účtovné oddelenie na tvorbu faktúr pre zdravotné poisťovne
za zadaný mesiac. Pre každú poisťovňu, ktorej pacient (pacienti) bol v zadaný
kalendárny mesiac hospitalizovaní aspoň jeden deň je potrebné pripraviť podklady
obsahujúce:
 kód zdravotnej poisťovne
 počet dní hospitalizácii (za všetkých pacientov – napr. 98 dní)
 výpis hospitalizovaných pacientov v jednotlivé dni mesiaca spolu s diagnózami
8. výpis aktuálne hospitalizovaných pacientov v nemocnici (identifikovaná svojím názvom)
9. výpis aktuálne hospitalizovaných pacientov v nemocnici (identifikovaná svojím
názvom), ktorí sú poistencami zadanej zdravotnej poisťovne (identifikovaná svojím
kódom)
10. výpis aktuálne hospitalizovaných pacientov v nemocnici (identifikovaná svojím názvom)
zotriedený podľa rodných čísel, ktorý sú poistencami zadanej zdravotnej poisťovne
(identifikovaná svojím kódom)
11. pridanie nemocnice
12. výpis nemocníc usporiadaných podľa názvov
13. zrušenie nemocnice (celá agenda sa presunie do inej nemocnice, ktorú špecifikuje
používateľ (identifikovaná svojím názvom), vrátane pacientov a historických záznamov)


Implementujte popísaný systém a demonštrujte jeho funkčnosť. Pri implementácii dbajte na
časovo efektívnu realizáciu požadovaných operácií a pamäťovú náročnosť použitých
údajových štruktúr a vyhnite sa použitiu rekurzie. Zabezpečte pri operácií č. 5 čo najmenšiu
zložitosť s využitím stromovej štruktúry! Dátumy neevidujte ako reťazce (použite vhodný dátový
typ).V dokumentácii uveďte výpočtovú zložitosť jednotlivých operácii. Nezabudnite na všeobecné
požiadavky semestrálnych prác (napr. generátor na naplnenie databázy...). V semestrálnej práci
vhodne využite minimálne jednu z týchto štruktúr: 2-3 strom, RB strom. Celá databáza sa musí dať
uložiť do textového súboru (súborov) vo fomáte csv (Comma-separated values) , tak aby bola
jednoducho importovateľná (v súbore sú iba potrebné údaje). Nie je nutné, aby boli všetky dáta v
jednom súbore. Veľkosť súboru(ov) má byť čo najmenšia.
Pracujte každý samostatne!


FOR THE LOVE OF GOD DO NOT OPEN THAT CODE 
