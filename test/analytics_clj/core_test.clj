(ns analytics-clj.core-test
  (:require [clojure.test :refer :all]
            [analytics-clj.core :refer :all])
  (:import (java.util UUID)))

(defonce analytics (initialize "foobarbaz"))

(defmacro testing-void
  "When you want to test a Java function that returns void.
  Just to be sure it doesn't throw an exception."
  [description & body]
  `(testing ~description
     (try
       ~@body
       (finally
         (is true)))))

(deftest test-initialize
  (testing "initialize an analytics client"
    (is (not (nil? analytics)))))

(deftest test-identify
  (testing-void "we're able to identify a user"
                (identify analytics "1234"))

  (testing-void "identify a user with traits"
                (identify analytics "1234" {"email" "foo@bar.com"}))

  (testing "identify a user with keyword traits"
    (with-redefs [traits* (fn [mb traits]
                            (is (= "email" (-> traits keys first))))]
      (identify analytics "1234" {:email "foo@bar.com"})))

  (testing "identify a user with namespaced keyword traits"
    (with-redefs [traits* (fn [mb traits]
                            (is (= "email/address" (-> traits keys first))))]
      (identify analytics "1234" {:email/address "foo@bar.com"})))

  (testing-void "identify an anonymous user"
                (identify analytics nil {} {:anonymous-id (UUID/randomUUID)})))

(deftest test-track
  (testing-void "track a simple event"
                (track analytics "1234" "signup"))

  (testing-void "track an event with custom properties"
                (track analytics "1234" "signup" {"company" "Acme Inc."})))
