#!/usr/bin/env stack
{- stack --install-ghc
    runghc
    --package amqp
    --package bytestring
    --package uuid
-}

-- Anti-virus AMQP example client

{-# LANGUAGE OverloadedStrings #-}

import Network.AMQP
import Data.UUID
import Data.UUID.V4
import Data.Text (Text)

-- import qualified Data.Text as T
-- import qualified Data.ByteString.Lazy.Char8 as BL


greeting :: String
greeting = "Example client"

brokerHost :: String
brokerHost = "localhost"

exchange :: Text
exchange = "check"

resultQueue :: Text
resultQueue = "av-result"

testMessage :: UUID -> Message
testMessage uuid = newMsg {
        msgID = Just $ toText uuid,
        msgType = Just "REQUEST",
        msgBody = "TEST MESSAGE"
    }

deliveryHandler :: (Message, Envelope) -> IO ()
deliveryHandler (msg, metadata) =
    putStrLn $ " * Received message:\n" ++ show msg

main :: IO ()
main = do
    putStrLn greeting
    putStrLn $ take (length greeting) (repeat '-')

    connection <- openConnection brokerHost "antivirus" "guest" "guest"
    channel <- openChannel connection

    uuid <- nextRandom

    putStrLn " * Sending message:"
    putStrLn $ show $ testMessage uuid

    -- send message
    publishMsg channel exchange "" (testMessage uuid)
    putStrLn "Data sent"

    -- receive message
    putStrLn " * Waiting for messages..."
    consumeMsgs channel resultQueue NoAck deliveryHandler

    -- wait for delivery
    getLine

    closeConnection connection
    putStrLn "Connection closed"
