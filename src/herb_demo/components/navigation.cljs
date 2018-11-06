(ns herb-demo.components.navigation
  (:require [garden.units :refer [px]]
            [goog.events :as events]
            [debux.cs.core :as d :refer-macros [clog clogn dbg dbgn break]]
            [goog.dom :as dom]
            [goog.events.EventType :as event-type]
            [herb-demo.components.text :refer [text]]
            [herb.core :refer-macros [<class defgroup]]
            [reagent.core :as r])
  (:require-macros [garden.def :refer [defcssfn]]))

(def sidebar-width (r/atom 256))

(def items {:intro {:label "Introduction"}
            :why-fns {:label "Why functions?"}
            :extending {:label "Extending style functions"}
            :key-meta {:label "The key matadata"}
            :group-meta {:label "The group metadata"
                         :sub {:defgroup {:label "defgroup macro"}}}})

(defgroup sidebar-style
  {:root {:background "#333"
          :position "fixed"
          :overflow-y "auto"
          :width (px @sidebar-width)
          :height "100%"
          :color "white"}
   :container {:padding (px 16)}
   :row {:padding-bottom (px 16)}})

(defn a-style []
  ^{:pseudo {:hover {:color "#3BABFF"}}}
  {:color "white"})

(defn sub-a-style []
  ^{:extend a-style}
  {:padding-left (px 16)})

(defn sidebar []
  [:section {:class (<class sidebar-style :root)}
   [:nav {:class (<class sidebar-style :container)}
    (doall
     (map (fn [[k v] index]
            ^{:key (:label v)}
            [:div
             [:div  {:class (<class sidebar-style :row)}
              [:a {:class (<class a-style)
                   :href (str "#" (name k))}
               [:span [:strong (str index ". ")] (:label v)]]]
             (when-let [sub (:sub v)]
               [:div {:class (<class sidebar-style :row)}
                (map (fn [[k v] sub-index]
                       ^{:key (:label v)}
                       [:a {:class (<class sub-a-style)
                            :href (str "#" (name k))}
                        [:span [:strong (str index "." sub-index " ")] (:label v)]])
                     sub
                     (map inc (range)))])])
          items
          (map inc (range))))]])


(def appbar-height 50)

(defcssfn calc
  [& args]
  [args])

(defgroup appbar-style
  {:root {:position "fixed"
          :background "#eee"
          :width (calc "100%" '- (px @sidebar-width))
          :align-items "center"
          :display "flex"
          :top 0
          :height (px appbar-height)}
   :column {:flex-basis "33%"}})

(defn underline
  [underline?]
  {:border-bottom (if underline? "1px solid rgba(0, 0, 0, 0.25)" "none")})

(defn on-scroll
  [state e]
  (if (> (.-y (dom/getDocumentScroll)) 0)
    (reset! state true)
    (reset! state false)))

(defn appbar
  []
  (let [scroll? (r/atom nil)]
    (r/create-class
     {:component-did-mount (fn []
                             (events/listen js/document
                                            event-type/SCROLL
                                            #(on-scroll scroll? %)))
      :reagent-render
      (fn []
        [:header {:class [(<class appbar-style :root)
                          (<class underline @scroll?)]}
         [:div {:class (<class appbar-style :column)}]
         [:div {:class (<class appbar-style :column)}
          [text {:variant :title
                 :align :center}
           "Herb: styling using functions"]]
         [:div {:class (<class appbar-style :column)}]])})))
