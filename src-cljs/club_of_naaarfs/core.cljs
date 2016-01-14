(ns club-of-naaarfs.core
  (:require [reagent.core :as reagent :refer [atom]]
            [clojure.string :as str]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [ajax.core :refer [GET POST]])
  (:import goog.History))

(enable-console-print!)


;; App Data
;;---------

(def hardcoded-proposals [{:title       "Nase Bohren"
                           :description "Mal was anderes als Arschkratzen"
                           :author      {:nickname   "Matzetias Hackmann!"
                                         :avatar-url "https://avatars.slack-edge.com/2015-11-13/14513569492_4ebac28ff715db6b5350_192.jpg"
                                         :email      "mwahlfälscher@cduhhh.com"}}

                          {:title       "Po Pieken"
                           :description "Mal was anderes als Nasebohren"
                           :author      {:nickname   "Flolowlowloooow"
                                         :avatar-url "https://secure.gravatar.com/avatar/86cbefc14488bbe76a1c2368189efc6c.jpg?s=512&d=https%3A%2F%2Fslack.global.ssl.fastly.net%2F7fa9%2Fimg%2Favatars%2Fava_0001-512.png"
                                         :email      "flooooooooo@bumbahumba.com"}}

                          {:title       "SchlauKacken"
                           :description "Besser als Nasebohren oder PoPieken"
                           :author      {:nickname   "Schrizdruff Stullrich"
                                         :avatar-url "https://avatars.slack-edge.com/2015-12-14/16662818098_1ecb9b85f3bdbc61aec0_192.jpg"
                                         :email      "stullrich@tomicowski.com"}}])


;; App State
;; ---------

(defonce app-state (atom {:proposals hardcoded-proposals
                          :search    ""}))


(defn update-search
  "Updates :search in app-state"
  [app-state new-search]
  (assoc app-state :search new-search))


;; Search logic
;;-------------

(defn matches-search?
  "Determines if a proposal item matches a text query"
  [search data]
  (let [qp (-> search (or "") str/lower-case re-pattern)]
    (->> (vals data)
         (filter string?)
         (map str/lower-case)
         (some #(re-find qp %)))))


;; View Components
;;----------------

(declare
  proposal-page
  proposal-search
  proposal-list
  proposal-item)


(defn proposal-page
  "Proposal page" []
  (let [{:keys [proposals search]} @app-state]
    [:div.contain
     [:div.row
      [:div.col-md-12
       [:img.proposal-img.pull-left {:src "/img/proposal.jpg"}]
       [:h1 "Proposals"]
       [proposal-search search]
       [proposal-list proposals search]]]]))

(defn proposal-search
  "A searchbox"
  [search]
  [:span "search, "
   [:input {:type      "text"
            :value     search
            :on-change (fn [event] (swap!
                                     app-state
                                     update-search
                                     (-> event .-target .-value)))}]]
  )

(defn proposal-list "An unordered list of proposals"
  [proposals search]
  [:div.container-fluid
   [:ul.proposal-list
    (for [proposal (->> proposals (filter #(matches-search? search %)))]
      ^{:key (:title proposal)} [proposal-item proposal])]])

(defn proposal-item "A proposal item component"
  [{:keys [title description] {:keys [nickname avatar-url]} :author}]
  [:li.proposal-item
   [:h4 title]
   [:p description]
   [:img.img-circle.img-thumb {:src avatar-url}]
   [:span.nickname nickname]])

(defn nav-link "Navigation item"
  [uri title page collapsed?]
  [:li {:class (when (= page (session/get :page)) "active")}
   [:a {:href     uri
        :on-click #(reset! collapsed? true)}
    title]])


(defn navbar "Main navigation" []
  (let [collapsed? (atom true)]
    (fn []
      [:nav.navbar.navbar-inverse.navbar-fixed-top
       [:div.container
        [:div.navbar-header
         [:button.navbar-toggle
          {:class         (when-not @collapsed? "collapsed")
           :data-toggle   "collapse"
           :aria-expanded @collapsed?
           :aria-controls "navbar"
           :on-click      #(swap! collapsed? not)}
          [:span.sr-only "Toggle Navigation"]
          [:span.icon-bar]
          [:span.icon-bar]
          [:span.icon-bar]]
         [:a.navbar-brand {:href "#/"} "Club of Naaarfs"]]
        [:div.navbar-collapse.collapse
         (when-not @collapsed? {:class "in"})
         [:ul.nav.navbar-nav
          [nav-link "#/" "Home" :home collapsed?]
          [nav-link "#/rich-says" "Rich says" :rich-says collapsed?]
          [nav-link "#/proposal" "Proposals" :proposal collapsed?]]]]])))

(defn rich-says-page "Rich's cites page " []
  [:div.contain
   [:div.row]
   [:div.col-md-6
    [:img.pull-left {:src "/img/rich.png"}]]
   [:div.col-md-6
    [:h1 "Rich says:"]
    [:blockquote>span.speech "Write code in Clojure or i kill you!!!"]]])


(defn home-page "Start Page" []
  [:div.container
   [:div.jumbotron
    [:h1 "Welcome to club_of_naaarfs"]
    [:p "Time to start building your site!"]
    [:p [:a.btn.btn-primary.btn-lg {:href "http://luminusweb.net"} "Learn more »"]]]
   [:div.row
    [:div.col-md-12
     [:h2 "Welcome to ClojureScript"]]]
   (when-let [docs (session/get :docs)]
     [:div.row
      [:div.col-md-12
       [:div {:dangerouslySetInnerHTML
              {:__html (md->html docs)}}]]])])

(def pages
  {:home      #'home-page
   :rich-says #'rich-says-page
   :proposal  #'proposal-page})

(defn page []
  [(pages (session/get :page))])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
                    (session/put! :page :home))

(secretary/defroute "/rich-says" []
                    (session/put! :page :rich-says))

(secretary/defroute "/proposal" []
                    (session/put! :page :proposal))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      HistoryEventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-docs! []
  (GET (str js/context "/docs") {:handler #(session/put! :docs %)}))

(defn mount-components []
  (reagent/render [#'navbar] (.getElementById js/document "navbar"))
  (reagent/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (fetch-docs!)
  (hook-browser-navigation!)
  (mount-components))
