Cezary Pawłowski

Maciej Korpalski

Paweł Tymiński 

# rule-induction #

[SAG] Indukcja reguł temporalnych na podstawie wielu równoległych szeregów czasowych

## Koncepcja ##

Projekt zostanie wykonany w języku Scala z użyciem biblioteki Akka.

System będzie się składał z jednego agenta dystrubującego dane oraz jednego lub więcej agentów wykonujących obliczenia.

Agent dystrubujący dane będzie "generatorem szeregu czasowego" dla agentów wykonujących obliczenia. 
Źródłem danych będzie zbiór danych związany z analizą koszykową (http://fimi.ua.ac.be/data/retail.dat).
Dane będą streamowane online z randomizowanym interwałem czasowym. 
Każda krotka będzie trafiała do jednego z węzłów.
Dane będą wysyłane w pętli bez końca.

Agent wykonujący obliczenia będzie wyszukiwał reguły asocjacyjne w zadanym oknie czasowym o określonym wsparciu i zaufaniu.
Wykorzystanym algorytmem wyszukiwania zbiorów częstych będzie algorytm apriori.
Węzły będą się ze sobą komunikować przesyłając na żądanie informacje o wsparciach.
Żądanie będzie takie wysyłane w przypadku jeśli różnica w odniesieniu do minimalnego wsparcia będzie nie większa niż pewna arbitralna liczba.
Wtenczas obliczone zostanie sumaryczne wsparcie, które zadecyduje o tym czy zbiór jest częsty.

Wygenerowane reguły bedą postaci: 

X -> Y [sup, conf, czas_start, czas_stop]

W ramach projektu zostaną porównane reguły wygenerowane z użyciem 1 i 3 agentów liczących w celu zbadania sensowności interakcji pomiędzy agentami wykonującymi obliczenia.
