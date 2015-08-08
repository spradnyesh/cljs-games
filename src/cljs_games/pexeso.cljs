(ns ^:figwheel-always cljs-games.pexeso
    (:require [reagent.core :as reagent :refer [atom]]))

(enable-console-print!)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; globals, app-state

(def alphabet (map char (range 65 90)))
(def num-pairs 8)
(defonce app-state (atom {:started? false :cards nil}))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; helpers

(defn filter-cards [key]
  (filter #(key @%) (:cards @app-state)))
(defn visible-cards [] (filter-cards :visible))
(defn matched-cards [] (filter-cards :matched))

(defn generate-cards []
  (mapv (fn [s] (atom {:smbl s :matched false :visible false}))
        (->> alphabet
             shuffle
             (take num-pairs)
             (repeat 2)
             flatten
             shuffle)))
(defn start-game []
  (swap! app-state assoc :started? true :cards (generate-cards)))
(defn game-over? []
  (= (* 2 num-pairs) (count (matched-cards))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; Components

(defn card [c]
  (letfn [(handle-0 [] (swap! c assoc :visible true))

          (handle-1 []
            (let [visible-card (first (visible-cards))]
              (when (= (:smbl @visible-card) (:smbl @c))
                (doseq [card [visible-card c]]
                  (swap! card assoc :matched true))))
            (if (game-over?)
              (when-let [ret (js/confirm "Yay, you've won! Do you want to play another game?")]
                (start-game))
              (handle-0)))

          (handle-2 []
            (doseq [card (:cards @app-state)]
              (swap! card assoc :visible false))
            (handle-0))

          (on-click! [event]
            (when-not (:visible @c)
              (condp = (count (visible-cards))
                0 (handle-0)
                1 (handle-1)
                2 (handle-2))))]

    [:div {:onClick on-click!}
     [:p {:class (cond (:matched @c) "matched"
                       (:visible @c) "visible"
                       :else "hidden")}
      [:span (:smbl @c)]]]))

(defn main []
  (if-not (:started? @app-state)
    (do (start-game) (main))
    [:div#pexeso (for [c (:cards @app-state)]
                   ^{:key (.random js/Math)}[:div.card [card c]])]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; init

(defn mount-root []
  (reagent/render [main] (.getElementById js/document "app")))
