-- name: create-user!
-- creates a new user record
INSERT INTO users
(id, nickname, email, avatar_url)
VALUES (:id, :nickname, :email, :avatar_url)

-- name: update-user!
-- update an existing user record
UPDATE users
SET nickname = :nickname, 
    email = :email, 
    avatar_url = :avatar_url
WHERE id = :id

-- name: get-user
-- retrieve a user given the id.
SELECT id, nickname, email, created, avatar_url FROM users
WHERE id = :id

-- name: delete-user!
-- delete a user given the id
DELETE FROM users
WHERE id = :id

-- name: create-proposal!
-- create a proposal record
INSERT INTO proposals
(id, title, description, author)
VALUES (:id, :title, :description, :author);

-- name: update-proposal!
-- update an existing proposal record
UPDATE proposals
SET title = :title, description = :description
WHERE id = :id

-- name get-proposal
-- retrieve a proposal for the given id.
SELECT id, title, description, author FROM proposals
WHERE id = :id

-- name: delete-proposal!
-- delete a proposal record
DELETE FROM proposals
WHERE id = :id

