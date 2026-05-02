import { useQuery } from "@tanstack/react-query";
import api from "../api/api";

export const useFetchMyShortUrls = (token, onError) => {
    return useQuery({
        queryKey: ["my-shortenurls"],
        queryFn: async () => {
            try {
                return await api.get("/api/urls/myurls", {
                    headers: {
                        "Content-Type": "application/json",
                        Accept: "application/json",
                        Authorization: "Bearer " + token,
                    },
                });
            } catch (error) {
                console.log("MYURLS ERROR:", error.response?.status, error.response?.data);
                throw error;
            }
        },
        select: (data) => {
            return data.data.sort(
                (a, b) => new Date(b.createdDate) - new Date(a.createdDate)
            );
        },
        // throwOnError: onError,   // ← commented out temporarily
        staleTime: 5000,
        enabled: !!token,
    });
};

export const useFetchTotalClicks = (token, onError) => {
    return useQuery({
        queryKey: ["url-totalclick"],
        queryFn: async () => {
            try {
                return await api.get(
                    "/api/urls/totalClicks?startDate=2024-01-01&endDate=2026-12-31",
                    {
                        headers: {
                            "Content-Type": "application/json",
                            Accept: "application/json",
                            Authorization: "Bearer " + token,
                        },
                    }
                );
            } catch (error) {
                console.log("TOTALCLICKS ERROR:", error.response?.status, error.response?.data);
                throw error;
            }
        },
        select: (data) => {
            return Object.keys(data.data).map((key) => ({
                clickDate: key,
                count: data.data[key],
            }));
        },
        // throwOnError: onError,   // ← commented out temporarily
        staleTime: 5000,
        enabled: !!token,
    });
};