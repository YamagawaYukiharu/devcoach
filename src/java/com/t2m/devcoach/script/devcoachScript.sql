/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 * Author:  master
 * Created: Sep 6, 2017
 */

CREATE TABLE public."ENDERECO"
(
  "RUA" character varying(30),
  "NUMERO" character varying(5),
  "BAIRRO" character varying(30),
  "CIDADE" character varying(20),
  "ESTADO" character varying(2),
  "PAIS" character varying(20),
  "ID_ENDERECO" integer NOT NULL,
  CONSTRAINT "FK_ID_ENDERECO" PRIMARY KEY ("ID_ENDERECO")
);

CREATE TABLE public."FERRAMENTA"
(
  "NOME_FERRAMENTA" character varying(20),
  "TIPO_FERRAMENTA" character varying(20),
  "ID" integer NOT NULL,
  CONSTRAINT "PK_ID_FERRAMENTA" PRIMARY KEY ("ID")
);

CREATE TABLE public."PESSOA"
(
  "DOCUMENTO" character varying(20) NOT NULL,
  "NOME" character varying(40),
  "DATANASC" date,
  "EMAIL" character varying(40),
  "ID" integer NOT NULL,
  "ID_ENDERECO" integer,
  CONSTRAINT "PK_ID_PESSOA" PRIMARY KEY ("ID"),
  CONSTRAINT "FK_ENDERECO" FOREIGN KEY ("ID_ENDERECO")
      REFERENCES public."ENDERECO" ("ID_ENDERECO") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);



CREATE TABLE public."PESSOA_TELEFONE"
(
  "ID" integer,
  "ID_PESSOA" integer,
  CONSTRAINT "FK_ID" FOREIGN KEY ("ID")
      REFERENCES public."TELEFONE" ("ID") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "FK_PESSOA" FOREIGN KEY ("ID_PESSOA")
      REFERENCES public."PESSOA" ("ID") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE public."PROGRAMA"
(
  "NOME_PROGRAMA" character varying(30),
  "ID" integer NOT NULL,
  "ID_PESSOA" integer,
  CONSTRAINT "PK_ID_PROGRAMA" PRIMARY KEY ("ID"),
  CONSTRAINT "FK_PESSOA" FOREIGN KEY ("ID_PESSOA")
      REFERENCES public."PESSOA" ("ID") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE public."PROGRAMA_SESSAO"
(
  "ID_PROGRAMA" integer,
  "ID_SESSAO" integer,
  CONSTRAINT "FK_ID_PROGRAMA" FOREIGN KEY ("ID_PROGRAMA")
      REFERENCES public."PROGRAMA" ("ID") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "FK_ID_SESSAO" FOREIGN KEY ("ID_SESSAO")
      REFERENCES public."SESSAO" ("ID") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE TABLE public."SESSAO"
(
  "NUMERO_DA_SESSAO" character varying(3),
  "DATA" character varying(10),
  "HORA" character varying(5),
  "FEEDBACK" character varying(400),
  "LICOES_APRENDIDAS" character varying(400),
  "ID" integer NOT NULL,
  CONSTRAINT "PK_ID_SESSAO" PRIMARY KEY ("ID")
);

CREATE TABLE public."SESSAO_FERRAMENTA"
(
  "ID_SESSAO" integer,
  "ID_FERRAMENTA" integer,
  CONSTRAINT "FK_ID_FERRAMENTA" FOREIGN KEY ("ID_FERRAMENTA")
      REFERENCES public."FERRAMENTA" ("ID") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT "FK_ID_SESSAO" FOREIGN KEY ("ID_SESSAO")
      REFERENCES public."SESSAO" ("ID") MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);




CREATE TABLE public."TELEFONE"
(
  "CODIGO_DO_PAIS" character varying(3),
  "CODIGO_DA_AREA" character varying(3),
  "NUMERO" character varying(10),
  "TIPO" character varying(20),
  "ID" integer NOT NULL,
  CONSTRAINT "PK_ID" PRIMARY KEY ("ID")
);

