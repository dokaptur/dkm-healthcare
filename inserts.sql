--OSOBY
insert into "dkm-healthcare".osoby(pesel,haslo,imie,nazwisko,data_urodzenia,adres,aptekarz,email) values ('14082319730',md5('raskolnikow'),'michal','dostojewski',date'1788-01-15','katowicka 13, petersburg', false, null);
insert into "dkm-healthcare".osoby(pesel,haslo,imie,nazwisko,data_urodzenia,adres,aptekarz,email) values ('07220302192',md5('podwiazka'),'bernard','riemann',date'1868-12-21','mila 77, berlin', false, 'hpoincare@uj.edu.pl');
insert into "dkm-healthcare".osoby(pesel,haslo,imie,nazwisko,data_urodzenia,adres,aptekarz,email) values ('48021514432',md5('ulises'),'james','joyce',date'1921-03-20','dublinska 411, dublin', false, 'ltolstoj@uj.edu.pl');
insert into "dkm-healthcare".osoby(pesel,haslo,imie,nazwisko,data_urodzenia,adres,aptekarz,email) values ('50012913591',md5('badumbum'),'tim','gowers',date'1911-01-01','londynska 34, londyn', false, null);


--LEKARZE
insert into "dkm-healthcare".lekarze values ('50012913591',true);
insert into "dkm-healthcare".lekarze values ('48021514432',true);
insert into "dkm-healthcare".lekarze values ('07220302192',true);
insert into "dkm-healthcare".lekarze values ('14082319730',false);

--OSOBY
insert into "dkm-healthcare".osoby(pesel,haslo,imie,nazwisko,data_urodzenia,lekarz_rodzinny,adres,aptekarz,email) values ('64050701526',md5('rozniczka'),'michal','swietek',date'1988-05-15','50012913591','gutenberga 4, krakow', false, 'michal.swietek@uj.edu.pl');
insert into "dkm-healthcare".osoby(pesel,haslo,imie,nazwisko,data_urodzenia,lekarz_rodzinny,adres,aptekarz,email) values ('46101807072',md5('konszfkapralka'),'kamil','jarosz',date'1992-11-19','07220302192','lojasiewicza 4, krakow', false, 'kjarosz@uj.edu.pl');
insert into "dkm-healthcare".osoby(pesel,haslo,imie,nazwisko,data_urodzenia,lekarz_rodzinny,adres,aptekarz,email) values ('35052119448',md5(''),'dudu','',date'1988-05-15','07220302192','gutenberga 4, krakow', false, 'dudu@dudu.tcs');
insert into "dkm-healthcare".osoby(pesel,haslo,imie,nazwisko,data_urodzenia,lekarz_rodzinny,adres,aptekarz,email) values ('20091508269',md5('calka'),'leonard','euler',date'1986-04-25','50012913591','gdzie 88, warszawa', true, 'leuler@uj.edu.pl');
insert into "dkm-healthcare".osoby(pesel,haslo,imie,nazwisko,data_urodzenia,lekarz_rodzinny,adres,aptekarz,email) values ('62080713094',md5('pochodna'),'stefan','banach',date'1989-05-15','07220302192','krakowska 45, krakow', false, 'sbanach@uj.edu.pl');
insert into "dkm-healthcare".osoby(pesel,haslo,imie,nazwisko,data_urodzenia,lekarz_rodzinny,adres,aptekarz,email) values ('91072415658',md5('styczna'),'stanislaw','mazur',date'1388-05-15','48021514432','lwowska 24, katowice', false, 'smazur@uj.edu.pl');
insert into "dkm-healthcare".osoby(pesel,haslo,imie,nazwisko,data_urodzenia,lekarz_rodzinny,adres,aptekarz,email) values ('05270102489',md5('homologie'),'henry','poincare',date'1968-05-25','48021514432','warszawska 2, paryz', false, 'hpoincare@uj.edu.pl');
insert into "dkm-healthcare".osoby(pesel,haslo,imie,nazwisko,data_urodzenia,lekarz_rodzinny,adres,aptekarz,email) values ('50090903529',md5('homokliniczny'),'lew','tolstoj',date'1888-11-11','48021514432','paryska 4, warszawa', false, 'ltolstoj@uj.edu.pl');



--PLACOWKI
insert into "dkm-healthcare".placowki values (1,'poradnia', 'poradnia wniebowstapienia','diabelna 13', 'radom', 7777777);
insert into "dkm-healthcare".placowki values (2,'poradnia', 'poradnia lezenia','szu 3', 'wola', 999);
insert into "dkm-healthcare".placowki values (3,'szpital', 'szpital dzieciecy','smierci 12', 'gdynia', 998);
insert into "dkm-healthcare".placowki values (4,'szpital', 'szpital mlodziezy','agoni 22', 'warszawa', 123321);
insert into "dkm-healthcare".placowki values (5,'przychodnia', 'przychodnia zamknieta','oplaty 99', 'sosnowiec', 997);
insert into "dkm-healthcare".placowki values (6,'przychodnia', 'przychodnia mocy','jadi 0', 'poznan', 992);


--SPECJALIZACJE
insert into "dkm-healthcare".specjalizacje values ('urolog',1);
insert into "dkm-healthcare".specjalizacje values ('anestezjolog',2);
insert into "dkm-healthcare".specjalizacje values ('ortopeda',3);
insert into "dkm-healthcare".specjalizacje values ('pediatra',4);
insert into "dkm-healthcare".specjalizacje values ('radiolog',5);
insert into "dkm-healthcare".specjalizacje values ('naurolog',6);
insert into "dkm-healthcare".specjalizacje values ('chirurg',7);
insert into "dkm-healthcare".specjalizacje values ('kardiolog',8);
insert into "dkm-healthcare".specjalizacje values ('ginekolog',9);
insert into "dkm-healthcare".specjalizacje values ('dentysta',10);


--ZABIEGI
insert into "dkm-healthcare".zabiegi(nazwa) values ('amputacja lewej nogi');
insert into "dkm-healthcare".zabiegi(nazwa) values ('amputacja prawej nogi');
insert into "dkm-healthcare".zabiegi(nazwa) values ('amputacja lewej dloni');
insert into "dkm-healthcare".zabiegi(nazwa) values ('amputacja prawej dloni');
insert into "dkm-healthcare".zabiegi(nazwa) values ('amputacja prawego przedramienia');
insert into "dkm-healthcare".zabiegi(nazwa) values ('amputacja lewego przedramienia');
insert into "dkm-healthcare".zabiegi(nazwa) values ('amputacja prawej reki');
insert into "dkm-healthcare".zabiegi(nazwa) values ('amputacja lewej reki');
insert into "dkm-healthcare".zabiegi(nazwa) values ('amputacja glowy');
insert into "dkm-healthcare".zabiegi(nazwa) values ('amputacja lewego malego palca stopy');



--OSOBY_INFO
insert into "dkm-healthcare".osoby_info(pesel,info,aktualne) values ('50012913591','nogi brak',true);
insert into "dkm-healthcare".osoby_info(pesel,info,aktualne) values ('48021514432','klepki 5. brak', true);
insert into "dkm-healthcare".osoby_info(pesel,info,aktualne) values ('48021514432','klepki 4. brak', false);
insert into "dkm-healthcare".osoby_info(pesel,info,aktualne) values ('07220302192','ziemniak zamiast serca',true);
insert into "dkm-healthcare".osoby_info(pesel,info,aktualne) values ('14082319730','lubi frytki',false);
insert into "dkm-healthcare".osoby_info(pesel,info,aktualne) values ('14082319730','lubi suche skarpetki',false);


--OSOBY_ALERGIE
insert into "dkm-healthcare".osoby_alergie(pesel,alergia,aktualne) values ('64050701526', 'brzydkie_dowody',true);
insert into "dkm-healthcare".osoby_alergie(pesel,alergia,aktualne) values ('35052119448', 'kąpiel',true);
insert into "dkm-healthcare".osoby_alergie(pesel,alergia,aktualne) values ('35052119448', 'slonce',true);
insert into "dkm-healthcare".osoby_alergie(pesel,alergia,aktualne) values ('05270102489', 'anglicy',true);

--OSOBY_LEKI
insert into "dkm-healthcare".osoby_leki(pesel,lek,od,"do") values ('91072415658', 'cukier',date'1986-04-25',date'1987-04-25');
insert into "dkm-healthcare".osoby_leki(pesel,lek,od,"do") values ('35052119448', 'herbata z cytryna, raz dziennie',date'1986-04-25',date'1987-04-25');
insert into "dkm-healthcare".osoby_leki(pesel,lek,od,"do") values ('46101807072', 'viagra',date'1986-04-25',date'1987-04-25');
insert into "dkm-healthcare".osoby_leki(pesel,lek,od,"do") values ('46101807072', 'jogurt',date'1986-04-25',date'1987-04-25');
insert into "dkm-healthcare".osoby_leki(pesel,lek,od,"do") values ('48021514432', 'rutinoscorbin, 3x dziennie',date'1986-04-25',date'1987-04-25');

--PACJENCI_SPECJALISCI
insert into "dkm-healthcare".pacjenci_specjalisci values ('64050701526', '48021514432');
insert into "dkm-healthcare".pacjenci_specjalisci values ('64050701526', '07220302192');
insert into "dkm-healthcare".pacjenci_specjalisci values ('46101807072', '07220302192');
insert into "dkm-healthcare".pacjenci_specjalisci values ('46101807072', '50012913591');
insert into "dkm-healthcare".pacjenci_specjalisci values ('35052119448', '50012913591');
insert into "dkm-healthcare".pacjenci_specjalisci values ('20091508269', '07220302192');
insert into "dkm-healthcare".pacjenci_specjalisci values ('62080713094', '07220302192');
insert into "dkm-healthcare".pacjenci_specjalisci values ('62080713094', '48021514432');
insert into "dkm-healthcare".pacjenci_specjalisci values ('91072415658', '48021514432');
insert into "dkm-healthcare".pacjenci_specjalisci values ('91072415658', '07220302192');
insert into "dkm-healthcare".pacjenci_specjalisci values ('91072415658', '50012913591');
insert into "dkm-healthcare".pacjenci_specjalisci values ('50090903529', '48021514432');
insert into "dkm-healthcare".pacjenci_specjalisci values ('50090903529', '07220302192');
insert into "dkm-healthcare".pacjenci_specjalisci values ('14082319730', '48021514432');
insert into "dkm-healthcare".pacjenci_specjalisci values ('14082319730', '07220302192');
insert into "dkm-healthcare".pacjenci_specjalisci values ('07220302192', '50012913591');
insert into "dkm-healthcare".pacjenci_specjalisci values ('07220302192', '48021514432');
insert into "dkm-healthcare".pacjenci_specjalisci values ('48021514432', '50012913591');
insert into "dkm-healthcare".pacjenci_specjalisci values ('48021514432', '07220302192');


--PLACOWKI_ZABIEGI
insert into "dkm-healthcare".placowki_zabiegi values ( 1, 1);
insert into "dkm-healthcare".placowki_zabiegi values ( 1, 2);
insert into "dkm-healthcare".placowki_zabiegi values ( 1, 3);
insert into "dkm-healthcare".placowki_zabiegi values ( 2, 4);
insert into "dkm-healthcare".placowki_zabiegi values ( 2, 6);
insert into "dkm-healthcare".placowki_zabiegi values ( 2, 7);
insert into "dkm-healthcare".placowki_zabiegi values ( 2, 9);
insert into "dkm-healthcare".placowki_zabiegi values ( 3, 2);
insert into "dkm-healthcare".placowki_zabiegi values ( 3, 3);
insert into "dkm-healthcare".placowki_zabiegi values ( 3, 5);
insert into "dkm-healthcare".placowki_zabiegi values ( 3, 7);
insert into "dkm-healthcare".placowki_zabiegi values ( 4, 1);
insert into "dkm-healthcare".placowki_zabiegi values ( 4, 3);
insert into "dkm-healthcare".placowki_zabiegi values ( 4, 5);
insert into "dkm-healthcare".placowki_zabiegi values ( 4, 7);
insert into "dkm-healthcare".placowki_zabiegi values ( 4, 9);
insert into "dkm-healthcare".placowki_zabiegi values ( 4, 2);
insert into "dkm-healthcare".placowki_zabiegi values ( 4, 4);
insert into "dkm-healthcare".placowki_zabiegi values ( 5, 10);
insert into "dkm-healthcare".placowki_zabiegi values ( 6, 10);


--RECEPTY

insert into "dkm-healthcare".recepty(pesel, id_lekarz, lek) values ('64050701526', '48021514432', 'czekolada 3x dziennie');
insert into "dkm-healthcare".recepty(pesel, id_lekarz, lek) values ('64050701526', '07220302192', 'czopki');
insert into "dkm-healthcare".recepty(pesel, id_lekarz, lek) values ('46101807072', '07220302192', 'gardlox');
insert into "dkm-healthcare".recepty(pesel, id_lekarz, lek) values ('46101807072', '50012913591', 'ciastka codziennie po 3');
insert into "dkm-healthcare".recepty(pesel, id_lekarz, lek) values ('35052119448', '50012913591', 'jabblko z gruszka');
insert into "dkm-healthcare".recepty(pesel, id_lekarz, lek) values ('20091508269', '07220302192', 'gripex');
insert into "dkm-healthcare".recepty(pesel, id_lekarz, lek) values ('62080713094', '07220302192', 'krople wody 8 razy na godzinę');
insert into "dkm-healthcare".recepty(pesel, id_lekarz, lek) values ('62080713094', '48021514432', 'apap');
insert into "dkm-healthcare".recepty(pesel, id_lekarz, lek) values ('91072415658', '48021514432', 'apap');


--SKIEROWANIA

insert into "dkm-healthcare".skierowania(pesel, id_lekarz, id_zabieg, od,"do") values ('62080713094', '07220302192', 1,date'1986-04-25',date'1987-04-25');
insert into "dkm-healthcare".skierowania(pesel, id_lekarz, id_zabieg, od,"do") values ('62080713094', '48021514432', 1,date'1986-04-25',date'1987-04-25');
insert into "dkm-healthcare".skierowania(pesel, id_lekarz, id_zabieg, od,"do") values ('91072415658', '48021514432', 2,date'1986-04-25',date'1987-04-25');
insert into "dkm-healthcare".skierowania(pesel, id_lekarz, id_zabieg, od,"do") values ('91072415658', '07220302192', 1,date'1986-04-25',date'1987-04-25');
insert into "dkm-healthcare".skierowania(pesel, id_lekarz, id_zabieg, od,"do") values ('91072415658', '50012913591', 3,date'1986-04-25',date'1987-04-25');
insert into "dkm-healthcare".skierowania(pesel, id_lekarz, id_zabieg, od,"do") values ('50090903529', '48021514432', 2,date'1986-04-25',date'1987-04-25');
insert into "dkm-healthcare".skierowania(pesel, id_lekarz, id_zabieg, od,"do") values ('50090903529', '07220302192', 1,date'1986-04-25',date'1987-04-25');
insert into "dkm-healthcare".skierowania(pesel, id_lekarz, id_zabieg, od,"do") values ('14082319730', '48021514432', 3,date'1986-04-25',date'1987-04-25');


--LEKARZE_PLACOWKI
insert into "dkm-healthcare".lekarze_placowki values ('07220302192', 1);
insert into "dkm-healthcare".lekarze_placowki values ('07220302192', 2);
insert into "dkm-healthcare".lekarze_placowki values ('07220302192', 4);
insert into "dkm-healthcare".lekarze_placowki values ('07220302192', 6);
insert into "dkm-healthcare".lekarze_placowki values ('50012913591', 3);
insert into "dkm-healthcare".lekarze_placowki values ('50012913591', 1);
insert into "dkm-healthcare".lekarze_placowki values ('50012913591', 5);
insert into "dkm-healthcare".lekarze_placowki values ('50012913591', 6);
insert into "dkm-healthcare".lekarze_placowki values ('48021514432', 5);
insert into "dkm-healthcare".lekarze_placowki values ('48021514432', 1);
insert into "dkm-healthcare".lekarze_placowki values ('48021514432', 2);
insert into "dkm-healthcare".lekarze_placowki values ('48021514432', 3);


--LEKARZE_SPECJALIZACJE
insert into "dkm-healthcare".lekarze_specjalizacje values ('07220302192', 1);
insert into "dkm-healthcare".lekarze_specjalizacje values ('07220302192', 2);
insert into "dkm-healthcare".lekarze_specjalizacje values ('50012913591', 3);
insert into "dkm-healthcare".lekarze_specjalizacje values ('50012913591', 4);
insert into "dkm-healthcare".lekarze_specjalizacje values ('50012913591', 5);
insert into "dkm-healthcare".lekarze_specjalizacje values ('48021514432', 6);
insert into "dkm-healthcare".lekarze_specjalizacje values ('48021514432', 7);
insert into "dkm-healthcare".lekarze_specjalizacje values ('48021514432', 8);
insert into "dkm-healthcare".lekarze_specjalizacje values ('48021514432', 9);
insert into "dkm-healthcare".lekarze_specjalizacje values ('48021514432', 10);

