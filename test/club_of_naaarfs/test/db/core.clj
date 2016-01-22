(ns club-of-naaarfs.test.db.core
  (:require [club-of-naaarfs.db.core :as db]
            [club-of-naaarfs.db.migrations :as migrations]
            [clojure.test :refer :all]
            [clojure.java.jdbc :as jdbc]
            [environ.core :refer [env]]
            [mount.core :as mount]))

(use-fixtures
  :once
  (fn [f]
    (migrations/migrate ["migrate"])
    (f)))

(deftest test-users
  (jdbc/with-db-transaction [t-conn db/conn]
    (jdbc/db-set-rollback-only! t-conn)
    (let [result (db/create-user<!
               {:nickname   "krissduff"
                :avatar_url "https://avatars.slack-edge.com/2015-12-14/16662818098_1ecb9b85f3bdbc61aec0_192.jpg"
                :email      "krizzle@shizz.le"} {:connection t-conn})
          id ((keyword "scope_identity()") result)]
           (let [user (db/get-user {:id id} {:connection t-conn})]
              (is 1 (count user))
              (is (contains? (first user) :created))
              (is (= { :id         id
                       :nickname   "krissduff"
                       :avatar_url "https://avatars.slack-edge.com/2015-12-14/16662818098_1ecb9b85f3bdbc61aec0_192.jpg"
                       :email      "krizzle@shizz.le"}
                 (doall (dissoc (first user) :created))))
              (is 1 (db/delete-user! {:id 1} {:connection t-conn}))))))
