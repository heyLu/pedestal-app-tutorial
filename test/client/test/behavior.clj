(ns client.test.behavior
  (:require [io.pedestal.app :as app]
            [io.pedestal.app.protocols :as p]
            [io.pedestal.app.tree :as tree]
            [io.pedestal.app.messages :as msg]
            [io.pedestal.app.render :as render]
            [io.pedestal.app.util.test :as test])
  (:use clojure.test
        client.behavior
        [io.pedestal.app.query :only [q]]))

(def inc-msg {msg/type :inc msg/topic [:my-counter]})

(deftest test-inc-transform
  (is (= (inc-transform nil inc-msg)
         1))
  (is (= (inc-transform 0 inc-msg)
         1))
  (is (= (inc-transform 1 inc-msg)
         2))
  (is (= (inc-transform 1 nil)
         2)))

(defn- data-model [app]
  (-> app :state deref :data-model))

(deftest test-app-state
  (let [app (app/build example-app)]
    (is (test/run-sync! app [{msg/type :inc msg/topic [:my-counter]}]
                        :begin :default))
    (is (= (data-model app)
           {:my-counter 1})))
  (let [app (app/build example-app)]
    (is (test/run-sync! app [{msg/type :inc msg/topic [:my-counter]}
                             {msg/type :inc msg/topic [:my-counter]}
                             {msg/type :inc msg/topic [:my-counter]}]
                        :begin :default))
    (is (= (data-model app)
           {:my-counter 3}))))
