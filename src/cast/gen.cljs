(ns cast.gen
  (:require [cljs.spec.alpha :as s]
            [cljs.spec.gen.alpha :as gen]
            [clojure.test.check.generators]))


(defn- previous-day
  "Create a new date object representing n days before the
   passed date. n is assumed to be 1 if it's not passed."
  ([day]
   (previous-day day 1))
  ([day n]
   (doto (js/Date. day)
     (.setDate (-> day (.getDate) (- n))))))


(defn- previous-month
  "Create a new date object n[1-11] months from today i.e the
   previous month of the given date object. n is assumed
   to be one if not passed."
  ([date]
   (previous-month date 1))
  ([date n]
   (doto (js/Date. date)
     (.setDate (-> date (.getDate) (- n) (+ 12) (rem 12))))))


(defn- date-at
  "Create a date object at a particular hour[0-23]."
  [x]
  (doto (js/Date.)
    (.setMinutes 0)
    (.setSeconds 0)
    (.setHours (inc x))))


(defn days-back
  "Generate n dates from today seperated by step days.
  When given a vector of 24 hour based time as schedule,
  it generates as many dates n times at those hours."
  ([n]
   (days-back n 1))
  ([n step]
   (->> (date-at 0)
        (iterate #(previous-day % step))
        (take n)))
  ([n step schedule]
   (letfn [(previous-days [dates]
             (map #(previous-day % step) dates))]
     (->> (map date-at schedule)
          (iterate previous-days)
          (take n)
          (flatten)
          (sort)))))


(defn months-back
  "Like `days-back`, it generates n dates from this month
   seperated by step months"
  ([n]
   (months-back n 1))
  ([n step]
   (->> (date-at 0)
        (iterate #(previous-month % step))
        (take n))))


(defn gen-trips [schedule terminals n min max]
  (let [dates      (days-back n 1 schedule)
        ranged-gen (s/gen (s/int-in min max))]
    (set (for [date dates terminal terminals]
           {:date     date
            :terminal terminal
            :new      (gen/generate ranged-gen)
            :repeat   (gen/generate ranged-gen)}))))

;; total tickets per hour - line
;; total new/repeat passenger tickets - lines
;; tickets per terminal - lines
;; driver trips per month - lines
;; miles travelled
;; max-speed/average speed
;; incidents/with fatalities
