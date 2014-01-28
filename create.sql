CREATE SCHEMA "dkm-healthcare";

CREATE SEQUENCE "dkm-healthcare".seq_id_zabieg START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE "dkm-healthcare".seq_nr_recepty START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE "dkm-healthcare".seq_nr_skier START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE "dkm-healthcare".seq_id_osoby_leki START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE "dkm-healthcare".seq_id_osoby_alergie START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE "dkm-healthcare".seq_id_osoby_info START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE "dkm-healthcare".seq_id_osoby_historia START WITH 1 INCREMENT BY 1;


CREATE TABLE "dkm-healthcare".osoby ( 
	pesel                char(11)  NOT NULL,
	haslo                char(32)  NOT NULL,
	n                    integer DEFAULT 1 NOT NULL,
	n_iteracja           char(32)  ,
	imie                 varchar(20)  NOT NULL,
	nazwisko             varchar(50)  NOT NULL,
	data_urodzenia       date  NOT NULL,
	lekarz_rodzinny      char(11)  ,
	historia_modyfikacja date DEFAULT current_date NOT NULL,
	adres                text  ,
	aptekarz             bool  ,
	admin                bool DEFAULT 'false' NOT NULL,
	email                text  ,
	CONSTRAINT pk_osoby PRIMARY KEY ( pesel )
 );
 
 CREATE TABLE "dkm-healthcare".lekarze ( 
	id_lekarz            char(11)  NOT NULL,
	prawa                bool DEFAULT 'true' NOT NULL,
	CONSTRAINT pk_lekarze PRIMARY KEY ( id_lekarz ),
	CONSTRAINT fk_lekarze FOREIGN KEY ( id_lekarz ) REFERENCES "dkm-healthcare".osoby( pesel ) ON DELETE CASCADE ON UPDATE CASCADE
 );

ALTER TABLE "dkm-healthcare".osoby add CONSTRAINT fk1_osoby FOREIGN KEY ( lekarz_rodzinny ) REFERENCES "dkm-healthcare".lekarze( id_lekarz ) ON DELETE SET NULL ON UPDATE CASCADE;

ALTER TABLE "dkm-healthcare".osoby ADD CONSTRAINT ck_1 CHECK ( pesel != lekarz_rodzinny );


CREATE TABLE "dkm-healthcare".placowki ( 
	id_placowka          numeric  NOT NULL,
	typ                  text  NOT NULL,
	nazwa                text  NOT NULL,
	adres                text  NOT NULL,
	miasto               text  NOT NULL,
	nr_tel               numeric  NOT NULL,
	CONSTRAINT pk_placowki PRIMARY KEY ( id_placowka )
 );

ALTER TABLE "dkm-healthcare".placowki ADD CONSTRAINT typ_check CHECK ( typ in ('poradnia', 'szpital', 'przychodnia') );

CREATE TABLE "dkm-healthcare".specjalizacje ( 
	nazwa                text  NOT NULL,
	id_spec              integer  NOT NULL,
	CONSTRAINT pk_specjalizacje PRIMARY KEY ( id_spec )
 );

CREATE TABLE "dkm-healthcare".zabiegi ( 
	id_zabieg            integer DEFAULT nextval('"dkm-healthcare".seq_id_zabieg') NOT NULL,
	nazwa                text  NOT NULL,
	CONSTRAINT pk_zabiegi PRIMARY KEY ( id_zabieg )
 );


CREATE TABLE "dkm-healthcare".osoby_info ( 
        id                   int DEFAULT nextval('"dkm-healthcare".seq_id_osoby_info'),
	pesel                char(11)  NOT NULL,
	info                 text  ,
	aktualne             bool,
	CONSTRAINT pk_osoby_info PRIMARY KEY ( pesel, info ),
	CONSTRAINT fk_osoby_info FOREIGN KEY ( pesel ) REFERENCES "dkm-healthcare".osoby( pesel ) ON DELETE CASCADE ON UPDATE CASCADE
 );
 
 CREATE TABLE "dkm-healthcare".osoby_alergie ( 
 	id		     int DEFAULT nextval('"dkm-healthcare".seq_id_osoby_alergie'),
	pesel                char(11)  NOT NULL,
	alergia              text  ,
	aktualne             bool NOT NULL,
	CONSTRAINT pk_osoby_alergie PRIMARY KEY ( pesel, alergia ),
	CONSTRAINT fk_osoby_alergie FOREIGN KEY ( pesel ) REFERENCES "dkm-healthcare".osoby( pesel ) ON DELETE CASCADE ON UPDATE CASCADE
 );
 
 CREATE TABLE "dkm-healthcare".osoby_leki ( 
        id                   int DEFAULT nextval('"dkm-healthcare".seq_id_osoby_leki'),
	pesel                char(11)  NOT NULL,
	lek                  text  ,
	od                   date NOT NULL DEFAULT current_date,
	"do"                 date NOT NULL,
	CONSTRAINT pk_osoby_leki PRIMARY KEY ( pesel, lek ),
	CONSTRAINT fk_osoby_leki FOREIGN KEY ( pesel ) REFERENCES "dkm-healthcare".osoby( pesel ) ON DELETE CASCADE ON UPDATE CASCADE
 );
 
 ALTER TABLE "dkm-healthcare".osoby_leki ADD CONSTRAINT ck_1 CHECK ( od < "do" );

 CREATE TABLE "dkm-healthcare".osoby_historia ( 
        id                   int DEFAULT nextval('"dkm-healthcare".seq_id_osoby_historia'),
	pesel                char(11)  NOT NULL,
	plik                 bytea,
	nazwa                varchar(200),
	CONSTRAINT pk_osoby_historia PRIMARY KEY ( pesel ),
	CONSTRAINT fk_osoby_leki FOREIGN KEY ( pesel ) REFERENCES "dkm-healthcare".osoby( pesel ) ON DELETE CASCADE ON UPDATE CASCADE
 );

--CREATE INDEX idx_osoby_info ON "dkm-healthcare".osoby ( lekarz_rodzinny );

CREATE TABLE "dkm-healthcare".pacjenci_specjalisci ( 
	pesel                char(11)  NOT NULL,
	id_lekarz            char(11)  NOT NULL,
	CONSTRAINT idx_pacjenci_specjalisci PRIMARY KEY ( pesel, id_lekarz ),
	--CONSTRAINT pk_pacjenci_specjalisci UNIQUE ( pesel ) ,
	--CONSTRAINT idx_pacjenci_specjalisci_0 UNIQUE ( id_lekarz ) ,
	CONSTRAINT fk_pacjenci_specjalisci FOREIGN KEY ( id_lekarz ) REFERENCES "dkm-healthcare".lekarze( id_lekarz ) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT fk_pacjenci_specjalisci_0 FOREIGN KEY ( pesel ) REFERENCES "dkm-healthcare".osoby( pesel ) ON DELETE CASCADE ON UPDATE CASCADE
 );

ALTER TABLE "dkm-healthcare".pacjenci_specjalisci ADD CONSTRAINT ck_0 CHECK ( pesel != id_lekarz );

CREATE TABLE "dkm-healthcare".placowki_zabiegi ( 
	id_placowka          numeric  NOT NULL,
	id_zabieg            integer  NOT NULL,
	CONSTRAINT idx_placowki_zabiegi PRIMARY KEY ( id_placowka, id_zabieg ),
	CONSTRAINT fk_placowki_zabiegi FOREIGN KEY ( id_placowka ) REFERENCES "dkm-healthcare".placowki( id_placowka ) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT fk_placowki_zabiegi_0 FOREIGN KEY ( id_zabieg ) REFERENCES "dkm-healthcare".zabiegi( id_zabieg ) ON DELETE CASCADE ON UPDATE CASCADE
 );

CREATE INDEX idx_placowki_zabiegi_0 ON "dkm-healthcare".placowki_zabiegi ( id_placowka );

CREATE INDEX idx_placowki_zabiegi_1 ON "dkm-healthcare".placowki_zabiegi ( id_zabieg );

CREATE TABLE "dkm-healthcare".recepty ( 
	numer                numeric DEFAULT nextval('"dkm-healthcare".seq_nr_recepty') NOT NULL,
	pesel                char(11)  NOT NULL,
	id_lekarz            char(11)  ,
	zrealizowana_przez   char(11)  ,
	lek                  text  NOT NULL,
	data_waznosci        date DEFAULT current_date + interval '3 month' ,
	zrealizowana         bool DEFAULT 'false' NOT NULL,
	CONSTRAINT pk_recepty PRIMARY KEY ( numer ),
	CONSTRAINT fk_recepty FOREIGN KEY ( pesel ) REFERENCES "dkm-healthcare".osoby( pesel ) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT fk_recepty_0 FOREIGN KEY ( id_lekarz ) REFERENCES "dkm-healthcare".lekarze( id_lekarz ) ON DELETE SET NULL ON UPDATE CASCADE,
	CONSTRAINT fk_recepty_1 FOREIGN KEY ( zrealizowana_przez ) REFERENCES "dkm-healthcare".osoby( pesel ) ON DELETE SET NULL ON UPDATE CASCADE
 );

CREATE INDEX idx_recepty ON "dkm-healthcare".recepty ( id_lekarz );

CREATE INDEX idx_recepty_0 ON "dkm-healthcare".recepty ( pesel );

CREATE INDEX idx_recepty_1 ON "dkm-healthcare".recepty ( zrealizowana_przez );

CREATE TABLE "dkm-healthcare".skierowania ( 
	numer                numeric DEFAULT nextval('"dkm-healthcare".seq_nr_skier') NOT NULL,
	pesel                char(11)  NOT NULL,
	id_lekarz            char(11)  ,
	id_zabieg            integer  NOT NULL,
	zrealizowany         bool DEFAULT 'false' NOT NULL,
	od                   date NOT NULL DEFAULT current_date,
	"do"                 date NOT NULL,	
	CONSTRAINT pk_skierowania PRIMARY KEY ( numer ),
	CONSTRAINT fk_skierowania_1 FOREIGN KEY ( id_zabieg ) REFERENCES "dkm-healthcare".zabiegi( id_zabieg ) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT fk_skierowania FOREIGN KEY ( pesel ) REFERENCES "dkm-healthcare".osoby( pesel ) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT fk_skierowania_0 FOREIGN KEY ( id_lekarz ) REFERENCES "dkm-healthcare".lekarze( id_lekarz ) ON DELETE SET NULL ON UPDATE CASCADE
 );
 
ALTER TABLE "dkm-healthcare".skierowania ADD CONSTRAINT ck_1 CHECK ( od < "do" );

CREATE INDEX idx_skierowania ON "dkm-healthcare".skierowania ( pesel );

CREATE INDEX idx_skierowania_0 ON "dkm-healthcare".skierowania ( id_lekarz );

CREATE INDEX idx_skierowania_1 ON "dkm-healthcare".skierowania ( id_zabieg );

CREATE TABLE "dkm-healthcare".lekarze_placowki ( 
	id_lekarz            char(11)  NOT NULL,
	id_placowka          numeric  NOT NULL,
	CONSTRAINT idx_lekarze_placowki PRIMARY KEY ( id_lekarz, id_placowka ),
	--CONSTRAINT pk_lekarze_placowki UNIQUE ( id_placowka ) ,
	CONSTRAINT fk_lekarze_placowki FOREIGN KEY ( id_placowka ) REFERENCES "dkm-healthcare".placowki( id_placowka ) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT fk_lekarze_placowki_0 FOREIGN KEY ( id_lekarz ) REFERENCES "dkm-healthcare".lekarze( id_lekarz ) ON DELETE CASCADE ON UPDATE CASCADE
 );

CREATE INDEX idx_lekarze_placowki_0 ON "dkm-healthcare".lekarze_placowki ( id_lekarz );

CREATE TABLE "dkm-healthcare".lekarze_specjalizacje ( 
	id_lekarz            char(11)  NOT NULL,
	id_spec              integer  NOT NULL,
	CONSTRAINT idx_lekarze_specjalizacje PRIMARY KEY ( id_lekarz, id_spec ),
	CONSTRAINT fk_lekarze_specjalizacje_0 FOREIGN KEY ( id_spec ) REFERENCES "dkm-healthcare".specjalizacje( id_spec ) ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT fk_lekarze_specjalizacje FOREIGN KEY ( id_lekarz ) REFERENCES "dkm-healthcare".lekarze( id_lekarz ) ON DELETE CASCADE ON UPDATE CASCADE
 );

CREATE INDEX idx_lekarze_specjalizacje_0 ON "dkm-healthcare".lekarze_specjalizacje ( id_lekarz );

CREATE INDEX idx_lekarze_specjalizacje_1 ON "dkm-healthcare".lekarze_specjalizacje ( id_spec );

CREATE VIEW "dkm-healthcare".historia_powiadomienia AS SELECT imie,nazwisko,email FROM "dkm-healthcare".osoby natural join "dkm-healthcare".osoby_info
where historia_modyfikacja = current_date order by pesel;

CREATE VIEW "dkm-healthcare".recepty_powiadomienia_5 AS SELECT imie,nazwisko,email, numer as "recepta" FROM "dkm-healthcare".osoby natural join "dkm-healthcare".recepty 
where zrealizowana = false and email is not null and 
extract(day from age(data_waznosci, current_date))=5  order by pesel;

CREATE VIEW "dkm-healthcare".recepty_powiadomienia_dzis AS SELECT imie,nazwisko,email, numer as "recepta" FROM "dkm-healthcare".osoby natural join "dkm-healthcare".recepty 
where zrealizowana = false and email is not null and 
data_waznosci=current_date order by pesel;

CREATE VIEW "dkm-healthcare".zabiegi_info AS SELECT z.nazwa, id_zabieg ,adres, miasto, nr_tel  from "dkm-healthcare".zabiegi z natural join "dkm-healthcare".placowki_zabiegi
join "dkm-healthcare".placowki p using (id_placowka) order by id_zabieg;



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
    x numeric; -- := nextval('"dkm-healthcare".seq_nr_recepty');
begin
		x = new.numer;
		if x is null then x = nextval('"dkm-healthcare".seq_nr_recepty');
		end if;
    while x in (select numer from "dkm-healthcare".recepty) loop
        x = nextval('"dkm-healthcare".seq_nr_recepty');
    end loop;
    new.numer=x;
    return new;
end;
$$ language plpgsql;;

create or replace function skier_check() returns trigger as $$
declare 
    x numeric; -- := nextval('"dkm-healthcare".seq_nr_skier');
begin
		x = new.numer;
		if x is null then x = nextval('"dkm-healthcare".seq_nr_skier');
		end if;
    while x in (select numer from "dkm-healthcare".skierowania) loop
        x = nextval('"dkm-healthcare".seq_nr_skier');
    end loop;
    new.numer=x;
    return new;
end;
$$ language plpgsql;;

create or replace function zabiegi_check() returns trigger as $$
declare 
    x numeric; -- := nextval('"dkm-healthcare".seq_id_zabieg');
begin
		x = new.id_zabieg;
		if x is null then x = nextval('"dkm-healthcare".seq_id_zabieg');
		end if;
    while x in (select id_zabieg from "dkm-healthcare".zabiegi) loop
        x = nextval('"dkm-healthcare".seq_id_zabieg');
    end loop;
    new.id_zabieg=x;
    return new;
end;
$$ language plpgsql;;


create trigger change_pesel_check before update on "dkm-healthcare".osoby for row
execute procedure change_pesel_check();;

create trigger pesel_check before insert on "dkm-healthcare".osoby
for each row execute procedure pesel_check();;

create trigger recepty_check before insert on "dkm-healthcare".recepty for row
execute procedure recepty_check();;

create trigger skier_check before insert on "dkm-healthcare".skierowania for row
execute procedure skier_check();;

create trigger zabieg_check before insert on "dkm-healthcare".zabiegi for row
execute procedure zabiegi_check();;




