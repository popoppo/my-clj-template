(ns guestbook.db.core
  (:use korma.db))


(defn connect-db []
  (defdb db (h2 {:db "./resources/db/site.db"})))
