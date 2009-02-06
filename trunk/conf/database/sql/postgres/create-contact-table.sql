-- Table: contact

-- DROP TABLE contact;

CREATE TABLE contact
(
  id serial NOT NULL,
  last_name character varying(64),
  first_name character varying(64),
  email character varying(64),
  rating integer,
  CONSTRAINT contact_pkey PRIMARY KEY (id)
)
WITHOUT OIDS;
ALTER TABLE contact OWNER TO feedserveruser;

INSERT INTO Contact (first_name, last_name, email, rating)
values ('Jim', 'Simon', 'jsimon@example.com', 5);

INSERT INTO Contact (first_name, last_name, email, rating)
values ('John', 'Doe', 'jdoe@example.com', 10); 