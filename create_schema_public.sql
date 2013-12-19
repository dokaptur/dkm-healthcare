--CREATE SCHEMA "dkm-healthcare";

CREATE SEQUENCE seq_id_zabieg START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE seq_nr_recepty START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE seq_nr_skier START WITH 1 INCREMENT BY 1;


CREATE TABLE osoby ( 
	pesel                char(11)  NOT NULL,
	haslo                char(32)  NOT NULL,
	n                    integer DEFAULT 1 NOT NULL,
	n_iteracja           char(32)  ,
	imie                 varchar(20)  NOT NULL,
	nazwisko             varchar(50)  NOT NULL,
	data_urodzenia       date  NOT NULL,
	adres                text  ,
	aptekarz             bool  ,
	admin                bool DEFAULT 'false' NOT NULL,
	email                text  ,
	CONSTRAINT pk_osoby PRIMARY KEY ( pesel )
 );


CREATE TABLE placowki ( 
	id_placowka          numeric  NOT NULL,
	typ                  text  NOT NULL,
	nazwa                text  NOT NULL,
	adres                text  NOT NULL,
	miasto               text  NOT NULL,
	nr_tel               numeric  NOT NULL,
	CONSTRAINT pk_placowki PRIMARY KEY ( id_placowka )
 );

ALTER TABLE placowki ADD CONSTRAINT typ_check CHECK ( typ in ('poradnia', 'szpital', 'przychodnia') );

CREATE TABLE specjalizacje ( 
	nazwa                text  NOT NULL,
	id_spec              integer  NOT NULL,
	CONSTRAINT pk_specjalizacje PRIMARY KEY ( id_spec )
 );

CREATE TABLE zabiegi ( 
	id_zabieg            integer DEFAULT nextval('seq_id_zabieg') NOT NULL,
	nazwa                text  NOT NULL,
	CONSTRAINT pk_zabiegi PRIMARY KEY ( id_zabieg )
 );

CREATE TABLE lekarze ( 
	id_lekarz            char(11)  NOT NULL,
	prawa                bool DEFAULT 'true' NOT NULL,
	CONSTRAINT pk_lekarze PRIMARY KEY ( id_lekarz ),
	CONSTRAINT fk_lekarze FOREIGN KEY ( id_lekarz ) REFERENCES osoby( pesel ) ON DELETE CASCADE ON UPDATE CASCADE
 );

CREATE TABLE osoby_info ( 
	pesel                char(11)  NOT NULL,
	alergie              text  ,
	inne                 text  ,
	przyjmowane_leki     text  ,
	modyfikacja_historii date DEFAULT current_date NOT NULL,
	lekarz_rodzinny      char(11)  ,
	CONSTRAINT pk_osoby_info PRIMARY KEY ( pesel ),
	CONSTRAINT fk_osoby_info FOREIGN KEY ( pesel ) REFERENCES osoby( pesel ) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT fk_osoby_info_0 FOREIGN KEY ( lekarz_rodzinny ) REFERENCES lekarze( id_lekarz ) ON DELETE SET NULL ON UPDATE CASCADE
 );

ALTER TABLE osoby_info ADD CONSTRAINT ck_1 CHECK ( pesel != lekarz_rodzinny );

CREATE INDEX idx_osoby_info ON osoby_info ( lekarz_rodzinny );

CREATE TABLE pacjenci_specjalisci ( 
	pesel                char(11)  NOT NULL,
	id_lekarz            char(11)  NOT NULL,
	CONSTRAINT idx_pacjenci_specjalisci PRIMARY KEY ( pesel, id_lekarz ),
	CONSTRAINT pk_pacjenci_specjalisci UNIQUE ( pesel ) ,
	CONSTRAINT idx_pacjenci_specjalisci_0 UNIQUE ( id_lekarz ) ,
	CONSTRAINT fk_pacjenci_specjalisci FOREIGN KEY ( id_lekarz ) REFERENCES lekarze( id_lekarz ) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT fk_pacjenci_specjalisci_0 FOREIGN KEY ( pesel ) REFERENCES osoby( pesel ) ON DELETE CASCADE ON UPDATE CASCADE
 );

ALTER TABLE pacjenci_specjalisci ADD CONSTRAINT ck_0 CHECK ( pesel != id_lekarz );

CREATE TABLE placowki_zabiegi ( 
	id_placowka          numeric  NOT NULL,
	id_zabieg            integer  NOT NULL,
	CONSTRAINT idx_placowki_zabiegi PRIMARY KEY ( id_placowka, id_zabieg ),
	CONSTRAINT fk_placowki_zabiegi FOREIGN KEY ( id_placowka ) REFERENCES placowki( id_placowka ) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT fk_placowki_zabiegi_0 FOREIGN KEY ( id_zabieg ) REFERENCES zabiegi( id_zabieg ) ON DELETE CASCADE ON UPDATE CASCADE
 );

CREATE INDEX idx_placowki_zabiegi_0 ON placowki_zabiegi ( id_placowka );

CREATE INDEX idx_placowki_zabiegi_1 ON placowki_zabiegi ( id_zabieg );

CREATE TABLE recepty ( 
	numer                numeric DEFAULT nextval('seq_nr_recepty') NOT NULL,
	pesel                char(11)  NOT NULL,
	id_lekarz            char(11)  ,
	zrealizowana_przez   char(11)  ,
	lek                  text  NOT NULL,
	data_waznosci        date DEFAULT current_date + interval '3 month' ,
	zrealizowana         bool DEFAULT 'false' NOT NULL,
	CONSTRAINT pk_recepty PRIMARY KEY ( numer ),
	CONSTRAINT fk_recepty FOREIGN KEY ( pesel ) REFERENCES osoby( pesel ) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT fk_recepty_0 FOREIGN KEY ( id_lekarz ) REFERENCES lekarze( id_lekarz ) ON DELETE SET NULL ON UPDATE CASCADE,
	CONSTRAINT fk_recepty_1 FOREIGN KEY ( zrealizowana_przez ) REFERENCES osoby( pesel ) ON DELETE SET NULL ON UPDATE CASCADE
 );

CREATE INDEX idx_recepty ON recepty ( id_lekarz );

CREATE INDEX idx_recepty_0 ON recepty ( pesel );

CREATE INDEX idx_recepty_1 ON recepty ( zrealizowana_przez );

CREATE TABLE skierowania ( 
	numer                numeric DEFAULT nextval('seq_nr_skier') NOT NULL,
	pesel                char(11)  NOT NULL,
	id_lekarz            char(11)  ,
	id_zabieg            integer  NOT NULL,
	zrealizowany         bool DEFAULT 'false' NOT NULL,
	CONSTRAINT pk_skierowania PRIMARY KEY ( numer ),
	CONSTRAINT fk_skierowania_1 FOREIGN KEY ( id_zabieg ) REFERENCES zabiegi( id_zabieg ) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT fk_skierowania FOREIGN KEY ( pesel ) REFERENCES osoby( pesel ) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT fk_skierowania_0 FOREIGN KEY ( id_lekarz ) REFERENCES lekarze( id_lekarz ) ON DELETE SET NULL ON UPDATE CASCADE
 );

CREATE INDEX idx_skierowania ON skierowania ( pesel );

CREATE INDEX idx_skierowania_0 ON skierowania ( id_lekarz );

CREATE INDEX idx_skierowania_1 ON skierowania ( id_zabieg );

CREATE TABLE lekarz_placowki ( 
	id_lekarz            char(11)  NOT NULL,
	id_placowka          numeric  NOT NULL,
	CONSTRAINT idx_lekarz_placowki PRIMARY KEY ( id_lekarz, id_placowka ),
	CONSTRAINT pk_lekarz_placowki UNIQUE ( id_placowka ) ,
	CONSTRAINT fk_lekarz_placowki FOREIGN KEY ( id_placowka ) REFERENCES placowki( id_placowka ) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT fk_lekarz_placowki_0 FOREIGN KEY ( id_lekarz ) REFERENCES lekarze( id_lekarz ) ON DELETE CASCADE ON UPDATE CASCADE
 );

CREATE INDEX idx_lekarz_placowki_0 ON lekarz_placowki ( id_lekarz );

CREATE TABLE lekarz_specjalizacja ( 
	id_lekarz            char(11)  NOT NULL,
	id_spec              integer  NOT NULL,
	CONSTRAINT idx_lekarz_specjalizacja PRIMARY KEY ( id_lekarz, id_spec ),
	CONSTRAINT fk_lekarz_specjalizacja_0 FOREIGN KEY ( id_spec ) REFERENCES specjalizacje( id_spec ) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT fk_lekarz_specjalizacja FOREIGN KEY ( id_lekarz ) REFERENCES lekarze( id_lekarz ) ON DELETE CASCADE ON UPDATE CASCADE
 );

CREATE INDEX idx_lekarz_specjalizacja_0 ON lekarz_specjalizacja ( id_lekarz );

CREATE INDEX idx_lekarz_specjalizacja_1 ON lekarz_specjalizacja ( id_spec );

CREATE VIEW historia_powiadomienia AS SELECT imie,nazwisko,email FROM osoby natural join osoby_info
where modyfikacja_historii = current_date order by pesel;

CREATE VIEW recepty_powiadomienia_5 AS SELECT imie,nazwisko,email, numer as "recepta" FROM osoby natural join recepty 
where zrealizowana = false and email is not null and 
extract(day from age(data_waznosci, current_date))=5  order by pesel;

CREATE VIEW recepty_powiadomienia_dzis AS SELECT imie,nazwisko,email, numer as "recepta" FROM osoby natural join recepty 
where zrealizowana = false and email is not null and 
data_waznosci=current_date order by pesel;

CREATE VIEW zabiegi_info AS SELECT z.nazwa, id_zabieg ,adres, miasto, nr_tel  from zabiegi z natural join placowki_zabiegi
join placowki p using (id_placowka) order by id_zabieg;


create or replace function add_osoba_info() returns trigger as $$
begin
    insert into osoby_info(pesel) values (new.pesel);
    return new;
end;
$$ language plpgsql;;

create or replace function change_pesel_check() returns trigger as $$
begin
    new.pesel=old.pesel;
    return new;
end;
$$ language plpgsql;;

create or replace function getchar(pesel char(11), i integer) returns integer as $$
begin
  return cast(substr(pesel, i, 1) as integer);
end;
$$ language plpgsql;;

create or replace function pesel_check() returns trigger as
$$
begin
	if (1 * getchar(new.pesel, 1) + 3 * getchar(new.pesel, 2) + 7 * getchar(new.pesel, 3) + 9 * getchar(new.pesel, 4) + 1 * getchar(new.pesel, 5) + 3 * getchar(new.pesel, 6) + 7 * getchar(new.pesel, 7) + 9 * getchar(new.pesel, 8) + 1 * getchar(new.pesel, 9) + 3 * getchar(new.pesel, 10) + getchar(new.pesel, 11)) % 10 = 0 then
    return new;
  else
    return null;
  end if;
end;
$$ language plpgsql;;

create or replace function recepty_check() returns trigger as $$
declare 
    x numeric := nextval('seq_nr_recepty');
begin
    while x in (select numer from recepty) loop
        x = nextval('seq_nr_recepty');
    end loop;
    new.numer=x;
    return new;
end;
$$ language plpgsql;;

create or replace function skier_check() returns trigger as $$
declare 
    x numeric := nextval('seq_nr_skier');
begin
    while x in (select numer from skierowania) loop
        x = nextval('seq_nr_skier');
    end loop;
    new.numer=x;
    return new;
end;
$$ language plpgsql;;

create or replace function zabiegi_check() returns trigger as $$
declare 
    x numeric := nextval('seq_id_zabieg');
begin
    while x in (select id_zabieg from zabiegi) loop
        x = nextval('seq_id_zabieg');
    end loop;
    new.id_zabieg=x;
    return new;
end;
$$ language plpgsql;;

create trigger add_osoba_info after insert on osoby for row
execute procedure add_osoba_info();;

create trigger change_pesel_check before update on osoby for row
execute procedure change_pesel_check();;

create trigger pesel_check before insert on osoby
for each row execute procedure pesel_check();;

create trigger recepty_check before insert on recepty for row
execute procedure recepty_check();;

create trigger skier_check before insert on skierowania for row
execute procedure skier_check();;

create trigger zabieg_check before insert on zabiegi for row
execute procedure zabiegi_check();;

