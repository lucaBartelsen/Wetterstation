--
-- PostgreSQL database dump
--

-- Dumped from database version 9.6.3
-- Dumped by pg_dump version 9.6.3

-- Started on 2017-07-06 14:34:04

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 186 (class 1259 OID 16533)
-- Name: Wetter; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE "Wetter" (
    id bigint NOT NULL,
    zeitstempel bigint NOT NULL,
    helligkeit numeric,
    luftdruck numeric,
    luftfeuchtigkeit numeric,
    temperatur numeric
);


ALTER TABLE "Wetter" OWNER TO postgres;

--
-- TOC entry 185 (class 1259 OID 16531)
-- Name: Wetter_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE "Wetter_id_seq"
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE "Wetter_id_seq" OWNER TO postgres;

--
-- TOC entry 2127 (class 0 OID 0)
-- Dependencies: 185
-- Name: Wetter_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: postgres
--

ALTER SEQUENCE "Wetter_id_seq" OWNED BY "Wetter".id;


--
-- TOC entry 2001 (class 2604 OID 16536)
-- Name: Wetter id; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "Wetter" ALTER COLUMN id SET DEFAULT nextval('"Wetter_id_seq"'::regclass);


--
-- TOC entry 2122 (class 0 OID 16533)
-- Dependencies: 186
-- Data for Name: Wetter; Type: TABLE DATA; Schema: public; Owner: postgres
--

INSERT INTO "Wetter" VALUES (1, 24989057, 54.23, 958.518, NULL, 34.08);
INSERT INTO "Wetter" VALUES (15, 24989071, 63.53, 958.408, 31.8, 33.17);
INSERT INTO "Wetter" VALUES (2, 24989058, 56.56, 958.525, 30.2, 34.11);
INSERT INTO "Wetter" VALUES (3, 24989059, 58.7, 958.494, 30.1, 34.19);
INSERT INTO "Wetter" VALUES (16, 24989072, 65.67, 958.406, 32.4, 32.85);
INSERT INTO "Wetter" VALUES (4, 24989060, 57.72, 958.459, 29.8, 34.15);
INSERT INTO "Wetter" VALUES (5, 24989061, 55.58, 958.443, 30.1, 34.15);
INSERT INTO "Wetter" VALUES (17, 24989073, 63.53, 958.423, 32.7, 32.53);
INSERT INTO "Wetter" VALUES (6, 24989062, 57.72, 958.441, 30.3, 34.12);
INSERT INTO "Wetter" VALUES (7, 24989063, 58.7, 958.438, 30.2, 34);
INSERT INTO "Wetter" VALUES (8, 24989064, 59.86, 958.461, 30.6, 33.86);
INSERT INTO "Wetter" VALUES (9, 24989065, 14.91, 958.459, 30.7, 34.08);
INSERT INTO "Wetter" VALUES (10, 24989066, 55.39, 958.466, 31.5, 33.84);
INSERT INTO "Wetter" VALUES (11, 24989067, 55.95, 958.491, 31, 34.18);
INSERT INTO "Wetter" VALUES (12, 24989068, 64.51, 958.389, 30.9, 34.11);
INSERT INTO "Wetter" VALUES (13, 24989069, NULL, 958.424, 31.1, 33.8);
INSERT INTO "Wetter" VALUES (14, 24989070, 61.39, 958.441, 31.4, 33.46);


--
-- TOC entry 2128 (class 0 OID 0)
-- Dependencies: 185
-- Name: Wetter_id_seq; Type: SEQUENCE SET; Schema: public; Owner: postgres
--

SELECT pg_catalog.setval('"Wetter_id_seq"', 17, true);


--
-- TOC entry 2003 (class 2606 OID 16541)
-- Name: Wetter Wetter_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY "Wetter"
    ADD CONSTRAINT "Wetter_pkey" PRIMARY KEY (id);


-- Completed on 2017-07-06 14:34:05

--
-- PostgreSQL database dump complete
--

