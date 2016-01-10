CREATE TABLE proposals (
   title VARCHAR,
   descriptiona TEXT,
   author int,
   created timestamp DEFAULT CURRENT_TIMESTAMP,
   foreign key (author) references users(id)
);
