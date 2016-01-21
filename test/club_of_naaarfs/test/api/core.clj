(ns club-of-naaarfs.test.api.core
  (:require [ring.mock.request :refer :all]
            [clojure.test :refer :all]
            [club-of-naaarfs.handler :refer :all]
            [cheshire.core :refer [generate-string, parse-string]]))

(defn add-headers 
  "add headers from a seq to a request"
  [req headers]
  (reduce (fn
                [req [header-name value]]
                (header req header-name value)) req headers))

(defn json-request
  "create a json request with the given method, uri and body"
  ([method uri]
    (json-request method uri nil {}))
  ([method uri payload]
    (json-request method uri payload {}))
  ([method uri payload headers]
  (let [pre-req (-> (request method uri)
                    (content-type "application/json")
                    (header "Accept" "application/json")
                    (add-headers headers))]
        (if (nil? payload) 
         pre-req 
         (body pre-req (generate-string payload))))))

(defn parse-response [response]
  "parse a json response body to a clojure structure"
  (parse-string (slurp (:body response)) true))

(defn test-api-route
  "test an api with your own request and expected status body and headers"
  ([req exp-status]
    (test-api-route req exp-status nil))
  ([req exp-status exp-body-fn]
    (test-api-route req exp-status exp-body-fn {}))
  ([req exp-status exp-body-fn exp-headers]
    (let [response (app req)]
      (is (= exp-status (:status response)))
      (when (not (nil? exp-body-fn))
        (is (not (false? (exp-body-fn (parse-response response))))))
      (testing 
        "response has expected headers"
        (map (fn [[name val]] ((is (name (:headers response) val)))) exp-headers)))))
