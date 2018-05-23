(ns cast.data
  (:require [cljs.spec.alpha :as s]
            [cljs.spec.gen.alpha :as gen]
            [clojure.set :as clj-set]
            [clojure.string :as string]
            [clojure.test.check.generators]))


;; Routes
(s/def ::destination
  #{"Abia, Aba" "Abuja, Utako" "Abuja, Abuja"
    "Lagos, Alaba" "Lagos, Ikotun" "Lagos, Jibowu"
    "Imo, Owerri" "Rivers, Portharcourt"})
(s/def ::start ::destination)
(s/def ::stop ::destination)
(s/def ::distance (s/and float? #(<= 1.0 %)))
(s/def ::route (s/keys :req-un [::distance ::start ::stop]))

;; Driver
(s/def ::firstname
  #{"Tomiwa" "Tomisin" "Uchenna" "Mbaike"
    "Kachi" "Blessing" "Sunday" "Sola"
    "Ebenezer" "Bolu" "Femi" "Nonso"
    "Wanda" "Lukman" "Nneka" "Tunde"})
(s/def ::surname
  #{"Uchemba" "Oladosu" "Fashina" "Osuigwe"
    "Anyadike" "Ikeze" "Odoh" "Adebajo"
    "Obasa" "Akintoye" "Kosoko" "Wura"})

(defn gen-n [g n] #(gen/fmap (comp string/upper-case string/join) (gen/vector g n)))

(s/def ::fullname (s/cat :firstname ::firstname :surname ::surname))
(s/def ::phone (s/with-gen (s/and string? #(-> % count (= 11)))
                 #(gen/fmap (fn [[s i r]] (apply str "0" s i r))
                            (gen/tuple (gen/elements [8 7]) (gen/elements [0 1])
                                       (gen/vector (gen/large-integer* {:min 1 :max 9}) 8)))))
(s/def ::license (s/with-gen (s/and string? #(-> % count (= 12)))
                   (gen-n (gen/char-alphanumeric) 12)))
(s/def ::travelled (s/int-in 7.0 21.0))
(s/def ::driver (s/keys :req-un [::fullname ::phone ::license ::travelled]))

;; Passenger
(s/def ::next-of-kin (s/keys :req-un [::fullname ::phone]))
(s/def ::passenger (s/keys :req-un [::fullname ::phone ::next-of-kin]))

;; Vehicle
(s/def ::class #{"Luxury Bus" "Hiace"})
(s/def ::brand #{"Toyota" "Honda"})
(s/def ::engine (s/with-gen (s/and string? #(-> % count (= 10)))
                  (gen-n (gen/large-integer* {:min 1 :max 9}) 10)))
(s/def ::registeration (s/with-gen (s/and string? #(-> % count (= 9)) #(-> % (nth 3) (= \-)))
                         #(gen/fmap (fn [[p n c]] (-> (apply str p "-" (concat n c)) string/upper-case))
                                    (gen/tuple (gen/elements ["APP" "IKJ" "BDG" "SUR" "LEK"])
                                               (gen/vector (gen/large-integer* {:min 1 :max 9}) 3)
                                               (gen/vector (gen/char-alpha) 2)))))
(s/def ::year (s/int-in 2010 2017))
(s/def ::capacity #{5 10 15 21 40 60 80})
(s/def ::vehicle (s/keys :req-un [::class ::brand ::engine ::registeration ::year ::capacity]))


;; manifest - list of tickets, driver, vehicle, route, depature-time, terminal(ignore)
;; Ticket
(s/def ::seat (s/int-in 1 80))
(s/def ::amount #{1500 2500 3000 4000 5000
                  6000 10000 12000 16000 21000})
(s/def ::ticket (s/keys :req-un [::passenger ::next-of-kin ::seat ::amount]))

;; Manifest
(s/def ::departure inst?)
(s/def ::terminal ::destination)
(s/def ::tickets (s/coll-of ::ticket :kind set? :count 80 :distinct true))
(s/def ::manifest (s/keys :req-un [::tickets ::driver ::vehicle ::route ::departure ::terminal]))

;; TODO: Post Spec Conformance
;; + Correct seats that:
;;   - have a greater index than the vehicle capacity
;;   - repeat in the set of seats in a manifest
;; + Create set of seats in manifest
;; + create set of unused seats in manifest
;; + Date creation in manifest
;; + Correct amount of tickets in each manifest
;; + Add terminal to manifest
