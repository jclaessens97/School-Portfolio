using COI.BL.Domain.Ideation;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace COI.BL.Algorithms
{
    public class TrendingAlgorithm : ITrendingAlgorithm
    {
        // number of days
        private const int PERIOD = 14;

        // arbitrary weight values 
        private const double COMMENT_WEIGHT = 0.6;
        private const double UPVOTE_WEIGHT = 0.4;
        private const double DOWNVOTE_WEIGHT = 0.1;

        /// <summary>
        /// Creates a list ordered from high to low by a calculated trending score per reply.
        /// </summary>
        /// <param name="replies"></param>
        /// <returns></returns>
        public async Task<IEnumerable<IdeationReply>> GetTrendingList(List<IdeationReply> replies)
        {
            var tasks = new List<Task<KeyValuePair<IdeationReply, double>>>();

            // Only check replies from latest <period> days.
            foreach(var reply in replies.Where(r => r.Created >= r.Created.AddDays(-PERIOD)))
            {
                // Create a Task for every reply to process later.
                tasks.Add(CalculateTrendingAsync(reply));
            }

            // Run all the CalculateTrending algorithms in parallel and catch results in a list of keyvalue-pairs.
            var replyTrendingScorePairsList = await Task.WhenAll(tasks);

            // Sort from high trending score to low
            var ordered = replyTrendingScorePairsList.AsEnumerable().OrderByDescending(pair => pair.Value);

            return ordered.Select(pair => pair.Key).ToList();
        }

        #region Helpers
        /// <summary>
        /// Calculates a trending score for individual reply and returns a keyvalue-pair with the reply linked to a score.
        /// </summary>
        /// <param name="reply"></param>
        /// <returns></returns>
        private async Task<KeyValuePair<IdeationReply, double>> CalculateTrendingAsync(IdeationReply reply)
        {
            double trendingScore = 0.0;

            // Values
            int numberOfComments = reply.Comments.Count;
            int numberOfDownVotes = reply.Downvotes;
            int numberOfUpVotes = reply.Upvotes;

            if (numberOfComments > 0)
            {
                var commentTimeSeries = reply
                    .Comments
                    .GroupBy(c => c.Created)
                    .ToDictionary(c => c.Key, c => c.Count());

                var commentsSd = CalculateSD(commentTimeSeries.Values);

                foreach (var day in commentTimeSeries.Keys)
                {
                    trendingScore += CalculateIndividualTrendingScore(
                        commentTimeSeries,
                        day,
                        commentsSd,
                        COMMENT_WEIGHT
                    );
                }
            }

            if (numberOfUpVotes > 0)
            {
                var upvoteTimeSeries = reply
                    .Votes
                    .Where(v => v.Value)
                    .GroupBy(v => v.Created)
                    .ToDictionary(v => v.Key, v => v.Count());

                var upvotesSd = CalculateSD(upvoteTimeSeries.Values);

                foreach (var day in upvoteTimeSeries.Keys)
                {
                    trendingScore += CalculateIndividualTrendingScore(
                        upvoteTimeSeries,
                        day,
                        upvotesSd,
                        UPVOTE_WEIGHT
                    );
                }
            }

            if (numberOfDownVotes > 0)
            {
                var downvoteTimeSeries = reply
                    .Votes
                    .Where(v => !v.Value)
                    .GroupBy(v => v.Created)
                    .ToDictionary(v => v.Key, v => v.Count());

                var downvotesSd = CalculateSD(downvoteTimeSeries.Values);

                foreach (var day in downvoteTimeSeries.Keys)
                {
                    trendingScore += CalculateIndividualTrendingScore(
                        downvoteTimeSeries,
                        day,
                        downvotesSd,
                        DOWNVOTE_WEIGHT
                    );
                }
            }

            return new KeyValuePair<IdeationReply, double>(reply, trendingScore);
        }

        /// <summary>
        /// Calculates the Z-score
        /// </summary>
        /// <param name="element"></param>
        /// <param name="mean"></param>
        /// <param name="sd"></param>
        /// <returns></returns>
        private double CalculateZScore(int element, double mean, double sd)
        {
            // If sd = 0, no point in calculating z score.
            if (sd > 0)
            {
                return (element - mean) / sd;
            }

            return mean;
        }

        /// <summary>
        /// Calculates the standard deviation of a list of values
        /// </summary>
        /// <see cref="https://stackoverflow.com/a/3141731/5985593"/>
        /// <param name="values"></param>
        /// <returns></returns>
        private double CalculateSD(IEnumerable<int> values)
        {
            double ret = 0;

            if (values.Count() > 0)
            {
                //Compute the Average      
                double avg = values.Average();
                //Perform the Sum of (value-avg)_2_2      
                double sum = values.Sum(d => Math.Pow(d - avg, 2));
                //Put it all together      
                ret = Math.Sqrt((sum) / (values.Count() - 1));
            }

            return ret;
        }

        /// <summary>
        /// Formula to calculate a trending score based on the parameters below.
        /// </summary>
        /// <param name="timeSeries"></param>
        /// <param name="day"></param>
        /// <param name="standardDeviation"></param>
        /// <param name="weight"></param>
        /// <returns></returns>
        private double CalculateIndividualTrendingScore(
            Dictionary<DateTime, int> timeSeries,
            DateTime day, 
            double standardDeviation,
            double weight
        )
        {
            double zScore = CalculateZScore(timeSeries[day], timeSeries.Values.Average(), standardDeviation);
            int daysBetweenNowAndThen = (DateTime.Today - day).Days;
            double decay = (daysBetweenNowAndThen / PERIOD);
            return (zScore / decay) * weight;
        }
        #endregion
    }
}
