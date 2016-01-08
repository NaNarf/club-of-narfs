-- name: create-user!
-- creates a new user record
INSERT INTO users
(id, nickname, email)
VALUES (:id, :nickname, :email)

-- name: update-user!
-- update an existing user record
UPDATE users
SET nickname = :nickname, email = :email
WHERE id = :id

-- name: get-user
-- retrieve a user given the id.
SELECT id, nickname, email FROM users
WHERE id = :id

-- name: delete-user!
-- delete a user given the id
DELETE FROM users
WHERE id = :id
