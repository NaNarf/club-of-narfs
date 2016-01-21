(ns club-of-naaarfs.test.api.users
  (:require [clojure.test :refer :all]
            [club-of-naaarfs.db.migrations :as migrations]
            [ring.mock.request :refer :all]
            [club-of-naaarfs.db.core :as db]
            [clojure.java.jdbc :as jdbc]
            [club-of-naaarfs.handler :refer :all]
            [club-of-naaarfs.test.api.core :refer :all]))

(use-fixtures
  :once
  (fn [f]
    (migrations/migrate ["migrate"])
    (f)))

(use-fixtures
  :each
  (fn [f]
      (jdbc/execute! db/conn ["delete from users"])
      (f)))

(def user1 {:avatar_url "http://example.com/avatar.png"
            :nickname "max_mustermann"
            :email "max@example.com"})
(def user2 {:avatar_url "http://example.com/avatar2.png"
            :nickname "marina_mustermann"
            :email "marina@example.com"})

(deftest test-get-users
  (testing "get empty users"
    (test-api-route (json-request :get "/users/") 200 empty?))
  (testing "create a user and get users"
    (test-api-route (json-request :post "/users/" user1) 200 
                    (fn [body] 
                      (is user1 (dissoc body :id :created))))
    (test-api-route (json-request :get "/users/") 200
                    (fn [body] 
                      (and (is 1 (count body))
                           (is user1 (dissoc (first body) :id :created)))))))

(deftest test-delete-user
  (testing "delete a created user"
    (let [req (json-request :post "/users/" user1)
          created-user (parse-response (app req))
          user-path (str "/user/" (created-user :id))]
      (test-api-route (json-request :delete user-path) 204)
      (test-api-route (json-request :get user-path) 404))))

(deftest test-update-user
  (testing "update an existing user"
    (let [req (json-request :post "/users/" user1)
          created-user (parse-response (app req))
          user-path (str "/user/" (created-user :id))]
      (test-api-route (json-request :put user-path user2) 200 
                      (fn [body]
                        (is user2 (dissoc body :id :created))))
      (test-api-route (json-request :get user-path) 200 (fn [body] (is user2 (dissoc body :id :created)))))))
