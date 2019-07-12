1. Name w buildObjects NIE JEST nazwą obiektu w bazie tylko nazwą urządzenia przypisanego do danego obiektu
2. DoorObjects jest ma wyżej wymienione nazwy dla drzwi, które łączy.
Przy dodawaniu do danych, trzeba będzie przekonwertować te nazwy na obcy klucz (foreign_key) łączonych pokoi.
3. Obiekty w programie zawierają tylko najważniejsze parametry.
Przy dodawaniu ich do bazy, trzeba pilnować o wypełnianiu brakujących pól (np. Lamp.dimmable=false)
