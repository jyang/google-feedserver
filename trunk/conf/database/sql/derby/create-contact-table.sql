CREATE TABLE Contact 
( id integer NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), 
  last_name VARCHAR(64),
  first_name VARCHAR(64),
  email VARCHAR(64),
  rating integer, 
  PRIMARY KEY (id));

INSERT INTO Contact (first_name, last_name, email, rating)
values ('Jim', 'Simon', 'jsimon@example.com', 5),
       ('John', 'Doe', 'jdoe@example.com', 10); 

