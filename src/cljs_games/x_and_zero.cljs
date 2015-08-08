(ns ^:figwheel-always cljs-games.x-and-zero
    (:require [reagent.core :as reagent :refer [atom]]
              [clojure.set :as set]))

(enable-console-print!)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; globals, app-state

(def num-cards 9)
(def line-indexes [#{0 1 2} #{3 4 5} #{6 7 8} ; horizontal
                   #{0 3 6} #{1 4 7} #{2 5 8} ; vertical
                   #{0 4 8} #{2 4 6}]) ; diagonal
(defonce app-state (atom {:started? false :cards nil :turn :x}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; helpers

(defn visible-cards [] (filter #(:visible @%) (:cards @app-state)))
(defn toggle-x-o []
  (if (= :x (:turn @app-state))
    (swap! app-state assoc :turn :o)
    (swap! app-state assoc :turn :x)))

(defn start-game []
  (swap! app-state assoc
         :started? true
         :cards (mapv (fn [i] (atom {:index i :smbl nil :visible false}))
                      (range num-cards))))

(defn game-draw? []
  (= num-cards (count (visible-cards))))
(defn game-over? []
  (let [turn (:turn @app-state)
        vc-indexes (into #{} (mapv (fn [c] (:index @c))
                                   (filter (fn [c] (= (name turn) (:smbl @c)))
                                           (visible-cards))))]
    (some true? (map #(= % (set/intersection vc-indexes %)) line-indexes))))
(defn game-draw-or-over? []
  (cond (game-draw?) :draw
        (game-over?) :over))

(defn restart-game? [rslt]
  (let [msg (str (if (= :draw rslt) "Draw!" "Yay, you've won!")
                 " Do you want to play another game?")]
    (when-let [ret (js/confirm msg)]
      (start-game))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Components

(defn card [c]
  (letfn [(on-click! [event]
            (when-not (:visible @c)
              (swap! c assoc :visible true
                     :smbl (name (:turn @app-state)))
              (if-let [rslt (game-draw-or-over?)]
                (restart-game? rslt)
                (toggle-x-o))))]

    [:div {:onClick on-click!}
     [:p {:class (if (:visible @c) "visible" "hidden")}
      [:span (:smbl @c)]]]))

(defn main []
  (if-not (:started? @app-state)
    (do (start-game) (main))
    [:div#x-zero (doall (for [c (:cards @app-state)]
                          ^{:key (:index @c)}[:div.card [card c]]))]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; init

(defn mount-root []
  (reagent/render [main] (.getElementById js/document "app")))
